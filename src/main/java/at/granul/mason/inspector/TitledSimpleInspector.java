package at.granul.mason.inspector;

import at.granul.mason.properties.Header;
import sim.display.GUIState;
import sim.engine.Stoppable;
import sim.portrayal.Inspector;
import sim.portrayal.inspector.PropertyInspector;
import sim.util.Interval;
import sim.util.Properties;
import sim.util.gui.LabelledList;
import sim.util.gui.NumberTextField;
import sim.util.gui.PropertyField;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * An Inspector that allows for collapsible sections (defined by Header properties) and for collapsible descriptions
 * defined by declared Properties.
 * <p/>
 * Based on SimpleInspector class from Mason, copyright 2006 by Sean Luke and George Mason University
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class TitledSimpleInspector extends Inspector {
    private static final long serialVersionUID = 1L;

    /** The GUIState  of the simulation */
    private GUIState state= null;

    /** The property list used for the layout - regenerated */
    private LabelledList propertyList = null;

    Section[] sections = null;

    /** The generated object properties -- this may change at any time */
    private Properties properties = null;

    private boolean updating = false;

    private JPanel header = new JPanel();

    private String listName = null;
    private JButton updateButton = null;



    GUIState getGUIState() {
        return state;
    }

    /** Creates a new TitledSimpleInspector with the given properties, state, maximum number of properties, and
     "name".  The name is what's shown in the labelled list of the TitledSimpleInspector.  It is not the
     title of the TitledSimpleInspector (what appears in a window).  For that, use setTitle. */
    public TitledSimpleInspector(Properties properties, GUIState state, String name) {
        super();
        setLayout(new BorderLayout());
        this.state = state;
        listName = name;
        header.setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        this.properties = properties;
        generateFromProperties();
        setTitle("" + properties.getObject());
    }

    /** Creates a new TitledSimpleInspector with the given object, state, maximum number of properties, and
     "name".  The name is what's shown in the labelled list of the TitledSimpleInspector.  It is not the
     title of the TitledSimpleInspector (what appears in a window).  For that, use setTitle. */
    public TitledSimpleInspector(Object obj, GUIState state, String name) {
        this(Properties.getProperties(obj), state, name);
    }

    JPopupMenu makePreliminaryPopup(final int index)
    {
        //Class type = properties.getType(index);
        if (properties.isComposite(index))
        {
            JPopupMenu popup = new JPopupMenu();
            JMenuItem menu = new JMenuItem("View");
            menu.setEnabled(true);
            menu.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    Properties props = properties;
                    final TitledSimpleInspector simpleInspector = new TitledSimpleInspector(props.getValue(index), TitledSimpleInspector.this.state, null);
                    final Stoppable stopper = simpleInspector.reviseStopper(
                            TitledSimpleInspector.this.state.scheduleRepeatingImmediatelyAfter(simpleInspector.getUpdateSteppable()));
                    TitledSimpleInspector.this.state.controller.registerInspector(simpleInspector,stopper);
                    JFrame frame = simpleInspector.createFrame(stopper);
                    frame.setVisible(true);
                }
            });
            popup.add(menu);
            return popup;
        }
        else return null;
    }

    protected String typeToName(Class typ) {
        if (typ == null) return null;
        if (typ.isPrimitive()) {
            return typ.toString();
        }
        else if (typ == String.class) {
            return "String";
        }
        else if (typ.isArray()) {
            final Class componentType = typ.getComponentType();
            //val convertedComponentType: Class[_] = getTypeConversion(componentType)
//      return typeToName(convertedComponentType) + "[]"
            return typeToName(componentType) + "[]";
        }
        else return null;
    }

    /** Private method.  Does a repaint that is guaranteed to work (on some systems, plain repaint())
     fails if there's lots of updates going on as is the case in our simulator thread.  */
    void doEnsuredRepaint(final Component component)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                if (component!=null) component.repaint();
            }
        });
    }

    private void generateFromProperties() {
        if (propertyList != null) remove(propertyList);
        propertyList = new LabelledList(listName);

        ArrayList<Section> sectionsList = new ArrayList<Section>();
        Section section = new Section();

        for (int i = 0; i < properties.numProperties(); i++) {
            if(properties.getType(i) == Header.class)
            {
                if(i > 0) {
                    sectionsList.add(section);
                    section.addToLabelledList(propertyList);
                }
                section = new HeadedSection(properties.getName(i));
            }
            else
               section.add(new PropertyInspectorField(properties, i));
        }
        sectionsList.add(section);
        sections = sectionsList.toArray(new Section[sectionsList.size()]);

        section.addToLabelledList(propertyList);
        add(propertyList, BorderLayout.CENTER);
        revalidate();
    }

    @Override
    public void setVolatile(boolean val) {
        super.setVolatile(val);
        if (isVolatile()) {
            if (updateButton != null) {
                header.remove(updateButton);
                revalidate();
            }
        }
        else {
            if (updateButton == null) {
                updateButton = (JButton) makeUpdateButton();
                final NumberTextField sacrificial = new NumberTextField(1, true);
                Dimension d = sacrificial.getPreferredSize();
                d.width = updateButton.getPreferredSize().width;
                updateButton.setPreferredSize(d);
                d = sacrificial.getMinimumSize();
                d.width = updateButton.getMinimumSize().width;
                updateButton.setMinimumSize(d);
                header.add(updateButton, BorderLayout.WEST);
                revalidate();
            }
        }
    }

    public void updateInspector() {
        if (!updating && properties.isVolatile()) {
            updating = true;
            generateFromProperties();
            doEnsuredRepaint(this);
            updating = false;
        }
        else {
            for (final Section section : sections)
                section.refresh();
        }
    }

    class Section {
        ArrayList<PropertyInspectorField> propertyFields = new ArrayList<PropertyInspectorField>();

        public void addToLabelledList(LabelledList list) {
            for (final PropertyInspectorField propertyInspectorField : propertyFields)
                propertyInspectorField.addToLabelledList(list);
        }

        public void refresh() {
            for (final PropertyInspectorField propertyInspectorField : propertyFields)
                propertyInspectorField.refresh();
        }

        public void add(PropertyInspectorField propertyInspectorField) {
            propertyFields.add(propertyInspectorField);
        }
    }

    class HeadedSection extends Section implements ToggleEventListener {
        JLabel label;
        Toggler sectionToggler;

        HeadedSection(String name) {
            label = new JLabel("<html>&nbsp;" + name + "</html>");
            label.setFont(new Font(label.getFont().getName(), Font.BOLD, (int) (label.getFont().getSize() * 1.2)));
        }

        @Override
        public void toggle(boolean expanded) {
            for (int i = 0; i < propertyFields.size(); i++)
                propertyFields.get(i).setVisible(expanded);
        }

        @Override
        public void refresh() {
            for (final PropertyInspectorField propertyInspectorField : propertyFields)
                propertyInspectorField.refresh(sectionToggler.isExpanded());
        }

        public void addToLabelledList(LabelledList list) {
            list.add(Box.createVerticalStrut(4));

            JPanel innerPanel = new JPanel();
            innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
            innerPanel.add(label);

            sectionToggler = new Toggler(this, true, true);
            innerPanel.add(sectionToggler);

            JPanel panel= new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
            panel.add(Box.createVerticalStrut(1));
            panel.add(innerPanel);
            panel.add(Box.createVerticalStrut(1));
            panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED));

            list.add(panel);
            list.add(Box.createVerticalStrut(2));

            final Toggler panelToggler = sectionToggler;
            panel.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    panelToggler.toggleSelection();
                }
            });
            super.addToLabelledList(list);
        }
    }

    class PropertyInspectorField implements ToggleEventListener{
        int propertyNumber;
        Properties properties;

        JLabel label = null;
        PropertyField field = null;
        JToggleButton toggle = null;

        Toggler infoToggler = null;
        JPanel infoPanel;
        Component strut = Box.createVerticalStrut(2);

        PropertyInspectorField(Properties properties, int propertyNumber) {
            this.propertyNumber = propertyNumber;
            this.properties = properties;
            buildComponents(properties);
        }

        public void addToLabelledList(LabelledList list) {
            list.add(infoToggler, label, toggle, field, null);
            if(infoPanel != null)
                list.add(infoPanel);
            list.add(strut);
        }

        public void refresh(boolean visible) {
            setVisible(visible && !properties.isHidden(propertyNumber));
            field.setValue(propertyToString(properties.getValue(propertyNumber)));
        }

        public void refresh() {
            refresh(true);
        }

        @Override
        public void toggle(boolean expanded) {
            infoPanel.setVisible(expanded);
        }

        private void setVisible(boolean visible){
            field.setVisible(visible);
            label.setVisible(visible);
            toggle.setVisible(visible);
            strut.setVisible(visible);
            if(infoToggler != null) {
                infoToggler.setVisible(visible);
                infoPanel.setVisible(visible && infoToggler.isExpanded());
            }
        }

        private void buildComponents(Properties properties) {
            label = new JLabel("<html>" + properties.getName(propertyNumber) + "&nbsp;&nbsp;</html>");
            field = makePropertyField(properties, propertyNumber);
            toggle = PropertyInspector.getPopupMenu(properties, propertyNumber, state, makePreliminaryPopup(propertyNumber));

            final String description = properties.getDescription(propertyNumber);

            if (description != null) {
                //Tooltip
                final String descriptionHTML = getTooltipHTML(properties.getDescription(propertyNumber));
                label.setToolTipText(descriptionHTML);
                toggle.setToolTipText(descriptionHTML);
                field.setToolTipText(descriptionHTML);

                //TextArea
                final JTextPane textArea = new JTextPane();
                textArea.setEditable(false);
                textArea.setContentType("text/html");
                textArea.setText("<html><body style='font-size: 100%'>" + properties.getDescription(propertyNumber) + "</html>");
                textArea.setMargin(new Insets(1,5,3,5));

                infoPanel = new JPanel(new BorderLayout()){
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(30, (int) super.getPreferredSize().getHeight());
                    }
                };
                infoPanel.add(textArea, BorderLayout.NORTH);

                infoToggler = new Toggler(this);
                label.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        infoToggler.toggleSelection();
                    }
                });
            }
        }

        final private String getTooltipHTML(String s) {
            return "<html><body style='width: 300px'>" + s + "</html>";
        }

        final PropertyField makePropertyField(final Properties props, final int index)
        {
            //logger.error("PropertyField.makePropertyField( " + index + ") " + properties.getName(index));
            final Class type = props.getType(index);
            return new PropertyField(
                    null,
                    propertyToString(props.getValue(index)),
                    props.isReadWrite(index),
                    props.getDomain(index),
                    (props.isComposite(index) ?
                            //PropertyField.SHOW_VIEWBUTTON :
                            PropertyField.SHOW_TEXTFIELD :
                            (type == Boolean.TYPE || type == Boolean.class ?
                                    PropertyField.SHOW_CHECKBOX :
                                    (props.getDomain(index) == null ? PropertyField.SHOW_TEXTFIELD :
                                            (props.getDomain(index) instanceof Interval) ?
                                                    PropertyField.SHOW_SLIDER : PropertyField.SHOW_LIST ))))
            {

                public void setValue(String value) {
                    if(!props.isReadWrite(index) && value.length() > 30){
                        super.setValue(value.substring(0,30) + "...");
                        setToolTipText(getTooltipHTML(value));
                    }
                    else
                        super.setValue(value);
                }

                // The return value should be the value you want the display to show instead.
                public String newValue(final String newValue)
                {
                    // UNUSUAL BUG: if I say this:
                    // Properties props = properties;
                    // ...or...
                    // Properties props = SimpleInspector.this.properties
                    // ... then sometimes props is set to null even though clearly
                    // properties is non-null above, since it'd be impossible to return a
                    // PropertyField otherwise.  So instead of declaring it as an instance
                    // variable here, we declare it as a final closure variable above.

                    // the underlying model could still be running, so we need
                    // to do this safely
                    synchronized(TitledSimpleInspector.this.state.state.schedule)
                    {
                        if (props.setValue(index, newValue) == null) java.awt.Toolkit.getDefaultToolkit().beep();
                        if (TitledSimpleInspector.this.state.controller != null) TitledSimpleInspector.this.state.controller.refresh();
                        return propertyToString(newValue);
                    }
                }
            };
        }

        final NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault());

        private final String propertyToString(Object obj)
        {
            if (obj == null) return "null";
            if (obj instanceof Number) {
                String value = numberFormatter.format(obj);
                //There should be no "." in Integers - else parsing will fail!
                if(obj instanceof Integer || obj instanceof Long)
                    return value.replace(".","");
                else
                    return value;
            }
            else
                return "" + obj;
        }

    }

}



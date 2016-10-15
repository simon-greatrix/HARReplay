package replay;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class NewSuite extends JPanel {
	private JLabel lblName;
	private JTextField textField;

	/**
	 * Create the panel.
	 */
	public NewSuite() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		lblName = new JLabel();
		lblName.setText("Name");
		add(lblName, "2, 2, fill, default");
		
		textField = new JTextField();
		add(textField, "4, 2, fill, default");
		textField.setColumns(30);
		
		JLabel lblSource = new JLabel("Source");
		add(lblSource, "2, 4");

	}

}

package org.kesler.simplereg.gui.operators;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.Color;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

import org.kesler.simplereg.logic.Operator;

/**
*<p>Используеся для отображения и редактирования списка операторов</p>
*
*
*/
public class OperatorsView extends JFrame {

	private OperatorsViewController controller;
	private OperatorsTableModel tableModel;
	private OperatorPanel operatorPanel;

	public OperatorsView(OperatorsViewController controller) {
		super("Операторы");
		this.controller = controller;
		tableModel = new OperatorsTableModel();
		operatorPanel = new OperatorPanel();
		createGUI();
	}

	private void createGUI() {
		this.setSize(500,500);

		JPanel mainPanel = new JPanel(new BorderLayout());

		GridBagConstraints c = new GridBagConstraints();
		JPanel tablePanel = new JPanel(new BorderLayout());

		JTable operatorsTable = new JTable(tableModel);
		TableColumn column = null;
		for (int i=0; i<5; i++) {
			column = operatorsTable.getColumnModel().getColumn(i);
			if (i == 1) {
				column.setPreferredWidth(200);
			} else {
				column.setMaxWidth(50);
			}
		}


		JScrollPane tableScrollPane = new JScrollPane(operatorsTable);

		// Панель кнопок управления списком операторов
		JPanel tableButtonPanel = new JPanel();

		JButton addButton = new JButton("Добавить");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				controller.newOperator();
			}
		});

		JButton editButton = new JButton("Редактировать");	
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				

			}
		});																										



		tableButtonPanel.add(addButton);
		tableButtonPanel.add(editButton);

		
		// добавляем на панель таблицы
		tablePanel.add(BorderLayout.CENTER, tableScrollPane);
		tablePanel.add(BorderLayout.SOUTH, tableButtonPanel);


		JPanel buttonPanel = new JPanel();

		JButton saveButton = new JButton("Сохранить");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				controller.saveOperators();
			}
		});

		JButton closeButton = new JButton("Закрыть");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
			}
		});

		buttonPanel.add(saveButton);
		buttonPanel.add(closeButton);


		mainPanel.add(BorderLayout.CENTER, tablePanel);
		mainPanel.add(BorderLayout.SOUTH, buttonPanel);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		this.add(mainPanel,BorderLayout.CENTER);
		this.setLocationRelativeTo(null);


	}


	public OperatorsTableModel getTableModel() {
		return tableModel;
	}

	public OperatorPanel getOperatorPanel(Operator operator) {
		operatorPanel.setOperator(operator);
		return operatorPanel;
	}

	// панель для диалога добавлеия оператора
	private class OperatorPanel extends JPanel implements ActionListener{
		private Operator operator;

		private JTextField fioTextField;
		private JTextField fioShortTextField;
		private JTextField passwordTextField;
		private JCheckBox controlerCheckBox;
		private JCheckBox adminCheckBox;
		private JCheckBox enabledCheckBox;


		public OperatorPanel () {
			super(new GridBagLayout());
			operator = new Operator();

			this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"Добавить/изменить"));

			JLabel fioLabel = new JLabel("ФИО");
			fioTextField = new JTextField(20);
			fioTextField.setActionCommand("FIO");
			fioTextField.addActionListener(this);

			JLabel fioShortLabel = new JLabel("ФИО сокр");
			fioShortTextField = new JTextField(20);
			fioShortTextField.setActionCommand("FIOShort");
			fioShortTextField.addActionListener(this);

			JLabel passwordLabel = new JLabel("Пароль");
			passwordTextField = new JTextField(20);
			passwordTextField.setActionCommand("password");
			passwordTextField.addActionListener(this);

			controlerCheckBox = new JCheckBox("Контролер",false);
			controlerCheckBox.setActionCommand("isContr");
			controlerCheckBox.addActionListener(this);

			adminCheckBox = new JCheckBox("Админ",false);
			adminCheckBox.setActionCommand("isAdmin");
			adminCheckBox.addActionListener(this);

			enabledCheckBox = new JCheckBox("Действ",true);
			enabledCheckBox.setActionCommand("enabled");
			enabledCheckBox.addActionListener(this);

			GridBagConstraints c = new GridBagConstraints();

			c.insets = new Insets(5,5,5,5);
			c.gridx = 0;
			c.gridy = 0;
			this.add(fioLabel,c);

			c.gridx = 1;
			c.gridy = 0;
			this.add(fioTextField,c);

			c.gridx = 0;
			c.gridy = 1;
			this.add(fioShortLabel,c);

			c.gridx = 1;
			c.gridy = 1;
			this.add(fioShortTextField,c);

			c.gridx = 0;
			c.gridy = 2;
			this.add(passwordLabel,c);

			c.gridx = 1;
			c.gridy = 2;
			this.add(passwordTextField,c);

			c.anchor = GridBagConstraints.WEST;
			c.gridx = 1;
			c.gridy = 3;
			this.add(controlerCheckBox,c);

			c.gridx = 1;
			c.gridy = 4;
			this.add(adminCheckBox,c);

			c.gridx = 1;
			c.gridy = 5;
			this.add(enabledCheckBox,c);

		}

		/**
		* Устанавливает приватное свойство - оператора
		* Заполняет поля сведениями об операторе
		*/
		public void setOperator(Operator operator) {
			this.operator = operator;

			fioTextField.setText(operator.getFIO());
			fioShortTextField.setText(operator.getFIOShort());
			passwordTextField.setText(operator.getPassword());
			controlerCheckBox.setSelected(operator.getIsControler());
			adminCheckBox.setSelected(operator.getIsAdmin());
			enabledCheckBox.setSelected(operator.getEnabled());
		}

		/**
		* заполняет поля оператора при изменении значений управляющих элементов
		*/
		public void actionPerformed(ActionEvent ev) {
			if ("FIO".equals(ev.getActionCommand())) {
				String fio = ((JTextField)ev.getSource()).getText();
				operator.setFIO(fio);
			} else if ("FIOShort".equals(ev.getActionCommand())) {
				String fioShort = ((JTextField)ev.getSource()).getText();
				operator.setFIOShort(fioShort);
			} else if ("password".equals(ev.getActionCommand())) {
				String password = ((JTextField)ev.getSource()).getText();
				operator.setPassword(password);
			} else if ("isContr".equals(ev.getActionCommand())) {
				Boolean isContr = ((JCheckBox)ev.getSource()).isSelected();
				operator.setIsControler(isContr);
			} else if ("isAdmin".equals(ev.getActionCommand())) {
				Boolean isAdmin = ((JCheckBox)ev.getSource()).isSelected();
				operator.setIsAdmin(isAdmin);
			} else if ("enabled".equals(ev.getActionCommand())) {
				Boolean enabled = ((JCheckBox)ev.getSource()).isSelected();
				operator.setEnabled(enabled);
			}
		}
	}

	// Модель данных для таблицы
	public class OperatorsTableModel extends AbstractTableModel {

		private List<Operator> operators;

		public OperatorsTableModel () {
			operators = new ArrayList<Operator>();
		}

		public void setOperators(List<Operator> operators) {
			this.operators = operators;
			fireTableDataChanged();
		}

		@Override 
		public int getRowCount() {
			return operators.size();
		}


		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public String getColumnName(int column) {
			String columnName = "Не опр.";
			switch (column) {
				case 0: columnName="№";
				break;
				case 1: columnName="ФИО";
				break;
				case 2: columnName="Контр";
				break;
				case 3: columnName="Админ";
				break;
				case 4: columnName="Действ";
				break;
			}
			return columnName;
		}

		@Override
		public Object getValueAt(int row, int column) {
			Operator operator = operators.get(row);
			Object value = null;
			switch (column) {
				case 0: value=operator.getId();
				break;
				case 1: value=operator.getFIO();
				break;
				case 2: value=operator.getIsControler();
				break;
				case 3: value=operator.getIsAdmin();
				break;
				case 4: value=operator.getEnabled();
				break;
			}
			return value;

		}

		@Override
		public Class getColumnClass(int column) {
			
			return getValueAt(0, column).getClass();

		}

		@Override
		public boolean isCellEditable(int row, int column) {
			boolean editable = true;
			if (column == 0) {
				editable = false;
			}

			return editable;
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			Operator operator = operators.get(row);
			switch (column) {
				case 1: operator.setFIO((String)aValue);
				break;
				case 2: operator.setIsControler((Boolean)aValue);
				break;
				case 3: operator.setIsAdmin((Boolean)aValue);
				break;
				case 4: operator.setEnabled((Boolean)aValue);
				break;
			}
			fireTableCellUpdated(row, column);
		} 

	}

}
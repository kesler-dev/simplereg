package org.kesler.simplereg.gui.main;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;

import java.beans.*;

import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;


import java.util.List;
import java.util.Arrays;


import org.kesler.simplereg.logic.Operator;


/**
* Диалог входа в систему
* 
*/
class LoginDialog extends JDialog{

	private Operator operator;

	private JComboBox loginComboBox;
	private JPasswordField passwordTextField;
	private boolean loginOk = false;

	private JOptionPane optionPane;

	private final String bth1String = "Ок";
	private final String btn2String = "Отмена";

	/**
	* Создает модальный диалог входа в систему
	* @param frame ссылка на родительское окно
	* @param operators список операторов 
	*/
	public LoginDialog(JFrame frame, List<Operator> operators) {
		super(frame,"Вход в систему", true);
		optionPane = createOptionPane();

		this.setContentPane(optionPane);
		this.setLocationRelativeTo(null);

		this.pack();
		setOperators(operators);

	}

	private JOptionPane createOptionPane() {

		JPanel dataPanel = new JPanel(new GridBagLayout());
		dataPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		JLabel loginLabel = new JLabel("Оператор: ");

		loginComboBox = new JComboBox();
		// при выборе оператора - запоминаем его свойтство
		loginComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				operator = (Operator) (loginComboBox.getSelectedItem());
			}

		});

		JLabel passwordLabel = new JLabel("Пароль: ");

		passwordTextField = new JPasswordField(20);
		// при нажатии Enter - нажимаем кнопку ОК
		passwordTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				optionPane.setValue(bth1String);
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		c.gridx = 0;
		c.gridy = 0;
		dataPanel.add(loginLabel, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		dataPanel.add(loginComboBox, c);
		c.fill = GridBagConstraints.NONE;

		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 1;
		dataPanel.add(passwordLabel, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		dataPanel.add(passwordTextField, c);

	
		optionPane = new JOptionPane(dataPanel,
									JOptionPane.PLAIN_MESSAGE,
									JOptionPane.OK_CANCEL_OPTION,
									null,
									new String[] {bth1String, btn2String},
									bth1String);
		
		optionPane.addPropertyChangeListener(new LoginPropertyChangeListener());

		return optionPane;

	}

	private void setOperators(List<Operator> operators) {
		loginComboBox.removeAllItems();
		for (Operator operator : operators) {
			loginComboBox.addItem(operator);
		}
	}

	/**
	* Открывает модальный диалог (ожидает закрытия окна диалога)
	*/
	public void showDialog() {
		setVisible(true);
	}


	/**
	* Возвращает флаг удачного входа
	*/
	public boolean isLoginOk() {
		return loginOk;
	}

	/**
	* Возвращает выбранного оператора
	*/
	public Operator getOperator() {
		return operator;
	}

	// public char[] getPassword() {
	// 	return passwordTextField.getPassword();
	// }


	private class LoginPropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent ev) {
			String prop = ev.getPropertyName();
			//JOptionPane optionPane = (JOptionPane)(ev.getSource());

			if (JOptionPane.VALUE_PROPERTY.equals(prop) ||
				JOptionPane.INPUT_VALUE_PROPERTY.equals(prop)) {
				
				Object value = optionPane.getValue();

				if (value == JOptionPane.UNINITIALIZED_VALUE) {
					return ;
				}

				optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

				if (bth1String.equals(value)) {
					char[] password = operator.getPassword().toCharArray();
					char[] input = passwordTextField.getPassword();
					if (input.length != password.length || !Arrays.equals(input, password)) {
						JOptionPane.showMessageDialog(null,
                										"Неправильный пароль. Попробуйте еще раз.",
                										"Ошибка",
                										JOptionPane.ERROR_MESSAGE);
						passwordTextField.selectAll();
						passwordTextField.requestFocusInWindow();

					} else {
						passwordTextField.setText(null);
						Arrays.fill(input,'0');
						Arrays.fill(password, '0');
						loginOk = true;
						setVisible(false);
						JOptionPane.showMessageDialog(null,
                										"Добро пожаловать, " + operator.getFIO() + "!",
                										"Добро пожаловать!",
                										JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					setVisible(false);
				}
				
			}
		}
	}

}

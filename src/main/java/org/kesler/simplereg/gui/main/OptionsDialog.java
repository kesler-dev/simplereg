package org.kesler.simplereg.gui.main;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import net.miginfocom.swing.MigLayout;

import org.kesler.simplereg.util.OptionsUtil;


/**
* Диалог настроек приложения
*/
public class OptionsDialog extends JDialog {

	private JComboBox dbComboBox;
	private JTextField serverTextField;
	private JTextField userTextField;
	private JTextField passwordTextField;

	private JTextField initRecStatusCodeTextField;

	private boolean changed = false;

	/**
	* Создает модальный диалог настроек программы
	* @param frame ссылка на родительское окно
	*/
	public OptionsDialog(JFrame frame) {
		super(frame, "Параметры приложения", true);

		createGUI();

		this.pack();
		this.setLocationRelativeTo(frame);

		loadGUIFromOptions();


	}

	/**
	* Открывает модальный диалог (ожидает закрытия окна диалога)
	*/
	public void showDialog() {
		setVisible(true);
	}


	private void createGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());

		JPanel dataPanel = new JPanel(new MigLayout("fill"));

		dbComboBox = new JComboBox();
		dbComboBox.addItem("h2 local");
		dbComboBox.addItem("h2 net");
		dbComboBox.addItem("mysql");
		dbComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				// Проверяем БД и для локальной удаляем и делаем неактивным сервер 
				String item = (String) dbComboBox.getSelectedItem();
				if (item.equals("h2 local")) {
					serverTextField.setText("");
					serverTextField.setEnabled(false);
				} else  {
					serverTextField.setEnabled(true);
				}

			}
		});

		serverTextField = new JTextField(15);
		userTextField = new JTextField(10);
		passwordTextField = new JTextField(15);

		JPanel dbPanel = new JPanel(new MigLayout("wrap 2",
													"[right][left]",
													""));
		dbPanel.setBorder(BorderFactory.createTitledBorder("Подключение"));

		dbPanel.add(new JLabel("База данных"));
		dbPanel.add(dbComboBox);
		dbPanel.add(new JLabel("Сервер"));
		dbPanel.add(serverTextField);
		dbPanel.add(new JLabel("Логин"));
		dbPanel.add(userTextField);
		dbPanel.add(new JLabel("Пароль"));
		dbPanel.add(passwordTextField);


		JPanel logicPanel = new JPanel(new MigLayout("wrap 2",
													"[right][left]",
													""));
		logicPanel.setBorder(BorderFactory.createTitledBorder("Служебные"));
		

		initRecStatusCodeTextField = new JTextField(5);

		logicPanel.add(new JLabel("Номер начального состояния для дела"));
		logicPanel.add(initRecStatusCodeTextField);




		dataPanel.add(dbPanel, "growx, wrap");
		dataPanel.add(logicPanel, "growx, wrap");

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("Сохранить");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (saveOptionsFromGUI()) {
					setVisible(false);
				}
			}
		});

		JButton cancelButton = new JButton("Отмена");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
			}
		});

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		mainPanel.add(dataPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		this.setContentPane(mainPanel);

	}

	private void loadGUIFromOptions() {

		Properties options = OptionsUtil.getOptions();


		String db = options.getProperty("db.driver");
		String server = options.getProperty("db.server");
		String user = options.getProperty("db.user");
		String password = options.getProperty("db.password");

		String initRecStatusCode = options.getProperty("logic.initRecStatusCode");

		dbComboBox.setSelectedItem(db);
		serverTextField.setText(server);
		userTextField.setText(user);
		passwordTextField.setText(password);

		initRecStatusCodeTextField.setText(initRecStatusCode);

	}

	private boolean saveOptionsFromGUI() {

		String db = (String) dbComboBox.getSelectedItem();
		String server = serverTextField.getText();
		String user = userTextField.getText();
		String password = passwordTextField.getText();

		String initRecStatusCode = initRecStatusCodeTextField.getText();

		if (user.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Поле Логин не может быть пустым", "Ошибка", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		try {
			Integer.parseInt(initRecStatusCode);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Код статуса должен быть числом", "Ошибка", JOptionPane.ERROR_MESSAGE);
			return false;
		}


		OptionsUtil.setOption("db.driver", db);
		OptionsUtil.setOption("db.server", server);
		OptionsUtil.setOption("db.user", user);
		OptionsUtil.setOption("db.password", password);

		OptionsUtil.setOption("logic.initRecStatusCode", initRecStatusCode);

		OptionsUtil.saveOptions();

		return true;
	}

}
package org.kesler.simplereg.gui.main;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.Box;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.Action;

import java.util.List;
import java.util.ArrayList;
import java.util.EnumSet;
import java.text.SimpleDateFormat;
import javax.swing.table.AbstractTableModel;

import org.kesler.simplereg.util.ResourcesUtil;

import org.kesler.simplereg.logic.reception.Reception;

/**
* Основной вид приложения. Содержит меню и управляющие компоненты для вызова дочерних окон. 
*/
public class MainView extends JFrame {


	private MainViewController controller = null;
	private MainViewReceptionsTableModel tableModel = null;
	private JLabel currentOperatorLabel;
	private List<Action> actions;

	private List<MainViewListener> listeners;


	public MainView(MainViewController controller) {
		super("Регистрация заявителей в Росреестре");

		this.controller = controller;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		List<Image> imageList = new ArrayList<Image>();
		imageList.add(ResourcesUtil.getIcon("cabinet.png").getImage());
		this.setIconImages(imageList);

		this.tableModel = new MainViewReceptionsTableModel();
		
		actions = new ArrayList<Action>();

		listeners = new ArrayList<MainViewListener>();


		// перебираем все комманды из перечисления
		for (MainViewCommand command : MainViewCommand.values()) {
			// Создаем действия с заданным обработчиком
			Action action = new AbstractAction() {
				public void actionPerformed(ActionEvent ev) {
					notifyListeners((MainViewCommand)getValue("MainViewCommand"));
				}
			};

			// определяем необходимые параметры
			action.putValue(Action.NAME, command.getName());
			action.putValue(Action.ACTION_COMMAND_KEY, command.getCommand());
			action.putValue("MainViewCommand",command);

			//добавлем в массив
			actions.add(action);
		}
				

		createGUI();
	}

	private void createGUI () {
		JPanel mainPanel = new JPanel(new BorderLayout());

		JMenuBar menuBar = new JMenuBar();
		// Основное меню
		JMenu mainMenu = new JMenu("Логин");

		// Пункт меню 
		JMenuItem loginMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.Login));
		loginMenuItem.setIcon(ResourcesUtil.getIcon("connect.png"));
		// Пункт меню
		JMenuItem logoutMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.Logout));
		logoutMenuItem.setIcon(ResourcesUtil.getIcon("disconnect.png"));
		// Пункт меню
		JMenuItem exitMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.Exit));
		exitMenuItem.setIcon(ResourcesUtil.getIcon("door_out.png"));

		// Формируем основное меню
		mainMenu.add(loginMenuItem);
		mainMenu.add(logoutMenuItem);
		mainMenu.add(exitMenuItem);

		// Меню статистики
		JMenu tasksMenu = new JMenu("Задачи");

		JMenuItem reestrMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.OpenReceptionsReestr));
		reestrMenuItem.setIcon(ResourcesUtil.getIcon(MainViewCommand.OpenReceptionsReestr.getIconName()));

		JMenuItem statisticMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.OpenStatistic));
		statisticMenuItem.setIcon(ResourcesUtil.getIcon(MainViewCommand.OpenStatistic.getIconName()));
		
		JMenuItem applicatorsMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.OpenApplicators));
		applicatorsMenuItem.setIcon(ResourcesUtil.getIcon(MainViewCommand.OpenApplicators.getIconName()));

		JMenuItem realtyObjectsMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.RealtyObjects));
		realtyObjectsMenuItem.setIcon(ResourcesUtil.getIcon(MainViewCommand.RealtyObjects.getIconName()));

		// Собираем меню задач
		tasksMenu.add(reestrMenuItem);
		tasksMenu.add(statisticMenuItem);
		tasksMenu.add(applicatorsMenuItem);
		tasksMenu.add(realtyObjectsMenuItem);

		// Меню справочников
		JMenu dictMenu = new JMenu("Справочники");


		JMenuItem servicesMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.Services));
		servicesMenuItem.setIcon(ResourcesUtil.getIcon(MainViewCommand.Services.getIconName()));

		JMenuItem statusesMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.ReceptionStatuses));
		statusesMenuItem.setIcon(ResourcesUtil.getIcon(MainViewCommand.ReceptionStatuses.getIconName()));

		JMenuItem realtyObjectTypesMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.RealtyObjectTypes));
		realtyObjectTypesMenuItem.setIcon(ResourcesUtil.getIcon(MainViewCommand.RealtyObjectTypes.getIconName()));


		
		dictMenu.add(servicesMenuItem);
		dictMenu.add(statusesMenuItem);
		dictMenu.add(realtyObjectTypesMenuItem);

		// Меню настроек
		JMenu optionsMenu = new JMenu("Настройки");


		JMenuItem operatorsMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.Operators));
		operatorsMenuItem.setIcon(ResourcesUtil.getIcon(MainViewCommand.Operators.getIconName()));

		JMenuItem optionsMenuItem = new JMenuItem(getActionByCommand(MainViewCommand.Options));
		optionsMenuItem.setIcon(ResourcesUtil.getIcon(MainViewCommand.Options.getIconName()));

		// Собираем меню настроек
		optionsMenu.add(operatorsMenuItem);
		optionsMenu.add(optionsMenuItem);

		// Меню О программе
		JMenu aboutMenu = new JMenu("О программе");

		// Формируем меню окна
		menuBar.add(mainMenu);
		menuBar.add(tasksMenu);
		menuBar.add(dictMenu);
		menuBar.add(optionsMenu);
		menuBar.add(aboutMenu);

		this.setJMenuBar(menuBar);

		Box buttonPanel = new Box(BoxLayout.X_AXIS);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		currentOperatorLabel = new JLabel();
		currentOperatorLabel.setBorder(BorderFactory.createRaisedBevelBorder());

		JButton newReceptionButton = new JButton(getActionByCommand(MainViewCommand.NewReception));
		newReceptionButton.setIcon(ResourcesUtil.getIcon(MainViewCommand.NewReception.getIconName()));

		JButton updateButton = new JButton(getActionByCommand(MainViewCommand.UpdateReceptions));
		updateButton.setIcon(ResourcesUtil.getIcon(MainViewCommand.UpdateReceptions.getIconName()));

		buttonPanel.add(new JLabel("Оператор: "));
		buttonPanel.add(currentOperatorLabel);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(newReceptionButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonPanel.add(updateButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(200,0)));

		// поправляем ширину столбцов, чтобы было покрасивей
		JTable receptionTable = new JTable(tableModel);
		receptionTable.getColumnModel().getColumn(0).setMinWidth(30);
		receptionTable.getColumnModel().getColumn(0).setMaxWidth(40);
		receptionTable.getColumnModel().getColumn(1).setMinWidth(100);
		receptionTable.getColumnModel().getColumn(1).setMaxWidth(500);

		JScrollPane receptionTableScrollPane = new JScrollPane(receptionTable);
		JPanel tablePanel = new JPanel(new GridLayout(1,0));
		tablePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		tablePanel.add(receptionTableScrollPane);

		mainPanel.add(BorderLayout.CENTER,tablePanel);
		mainPanel.add(BorderLayout.NORTH,buttonPanel);

		this.add(mainPanel, BorderLayout.CENTER);	
	
		this.setSize(800,600);

		this.setLocationRelativeTo(null);
		// this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	public MainViewReceptionsTableModel getTableModel () {
		return tableModel;
	}

	public void setCurrentOperatorLabel(String text) {
		currentOperatorLabel.setText(text);
	}

	class MainViewReceptionsTableModel extends AbstractTableModel {
		private List<Reception> receptions;

		public MainViewReceptionsTableModel() {
			this.receptions = new ArrayList<Reception>();
		}

		public void setReceptions(List<Reception> receptions) {
			this.receptions = receptions;
			fireTableDataChanged();
		}

		public int getRowCount() {
			return receptions.size();
		}

		public int getColumnCount() {
			return 4;
		}

		public String getColumnName(int column) {
			String columnName = "Не опр";
			switch (column) {
					case 0: columnName = "№";
					break;
					case 1: columnName = "Создано";
					break;
					case 2: columnName = "Заявители";
					break;
					case 3: columnName = "Услуга";
					break;
				}
				return columnName;

		}

		public Object getValueAt(int row, int column) {
			Object value = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			Reception reception = receptions.get(row);
			if (reception != null) {
				switch (column) {
					case 0: value = reception.getId();
					break;
					case 1: value = dateFormat.format(reception.getOpenDate());
					break;
					case 2: value = reception.getApplicatorsNames();
					break;
					case 3: value = reception.getServiceName();
					break;
				}

			}

			return value;

		}
	}


	public Action getActionByCommand(MainViewCommand command) {
		Action selectedAction = null;
		for (Action action: actions) {
			if (command.equals(action.getValue("MainViewCommand"))) {
				selectedAction = action;
			}
		}

		return selectedAction;
	}

	public void addMainViewListener(MainViewListener listener) {
		listeners.add(listener);
	}

	private void notifyListeners(MainViewCommand command) {
		for (MainViewListener listener : listeners) {
			listener.performMainViewCommand(command);
		}
	}


}
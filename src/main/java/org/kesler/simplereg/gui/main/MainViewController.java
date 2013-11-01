package org.kesler.simplereg.gui.main;

import java.util.List;
import java.util.Arrays;
import javax.swing.JOptionPane;

import org.kesler.simplereg.logic.reception.ReceptionsModel;
import org.kesler.simplereg.logic.reception.Reception;
import org.kesler.simplereg.logic.reception.ReceptionsModelStateListener;
import org.kesler.simplereg.logic.reception.ReceptionsModelState;
import org.kesler.simplereg.logic.operator.Operator;
import org.kesler.simplereg.logic.operator.OperatorsModel;
import org.kesler.simplereg.logic.operator.OperatorsModelState;
import org.kesler.simplereg.logic.operator.OperatorsModelStateListener;
import org.kesler.simplereg.logic.realty.RealtyObject;
import org.kesler.simplereg.logic.realty.RealtyObjectsModel;
import org.kesler.simplereg.gui.util.ProcessDialog;
import org.kesler.simplereg.gui.util.InfoDialog;
import org.kesler.simplereg.util.HibernateUtil;

import org.kesler.simplereg.gui.services.ServicesDialogController;
import org.kesler.simplereg.gui.operators.OperatorListDialogController;
import org.kesler.simplereg.gui.statistic.StatisticViewController;
import org.kesler.simplereg.gui.reception.MakeReceptionViewController;
import org.kesler.simplereg.gui.reception.ReceptionStatusListDialogController;
import org.kesler.simplereg.gui.applicator.FLListDialogController;
import org.kesler.simplereg.gui.applicator.ULListDialogController;
import org.kesler.simplereg.gui.reestr.ReestrViewController;
import org.kesler.simplereg.gui.realty.RealtyObjectListDialogController;
import org.kesler.simplereg.gui.realty.RealtyTypeListDialogController;



/**
* Управляет основным окном приложения
*/
public class MainViewController implements MainViewListener, 
								CurrentOperatorListener, 
								OperatorsModelStateListener, 
								ReceptionsModelStateListener{
	private static MainViewController instance;

	private MainView mainView;
	private ReceptionsModel receptionsModel;
	private OperatorsModel operatorsModel;
	private RealtyObjectsModel realtyObjectsModel;
	private LoginDialog loginDialog;

	private ProcessDialog processDialog;

	private MainViewController() {
		this.receptionsModel = ReceptionsModel.getInstance();
		this.operatorsModel = OperatorsModel.getInstance();
		this.realtyObjectsModel = RealtyObjectsModel.getInstance();

		operatorsModel.addOperatorsModelStateListener(this);
		receptionsModel.addReceptionsModelStateListener(this);
		
		mainView = new MainView(this);
		mainView.addMainViewListener(this);
		
		CurrentOperator.getInstance().addCurrentOperatorListener(this);
	}

	/**
	* Всегда возвращает один и тот же экземпляр контроллера (паттерн Одиночка)
	*/
	public static synchronized MainViewController getInstance() {
		if (instance == null) {
			instance = new MainViewController();
		}
		return instance;
	}

	/**
	* Открывает основное окно приложения
	*/
	public void openMainView() {
		mainView.setVisible(true);
		setMainViewAccess(null);
	}

	/**
	* Обрабатывает команды основного вида, определенные в классе {@link org.kesler.simplereg.gui.main.MainViewCommand}
	*/
	@Override
	public void performMainViewCommand(MainViewCommand command) {
		switch (command) {
			case Login: 
				login();
				break;
			case Logout:
				logout();
				break;	
			case NewReception: 
				openMakeReceptionView();
				break;
			case UpdateReceptions: 
				readReceptions();
				break;
			case OpenReceptionsReestr: 
				openReceptionsReestr();
				break;
			case OpenStatistic: 
				openStatistic();
				break;
			case FLs: 
				openFLs();
				break;
			case ULs: 
				openULs();
				break;
			case Services: 
				openServicesView();
				break;
			case ReceptionStatuses: 
				openReceptionStatuses();
				break;
			case RealtyObjects: 
				openRealtyObjects();
				break;
			case RealtyObjectTypes: 
				openRealtyObjectTypes();
				break;
			case Operators: 
				openOperators();
				break;
			case Options:	
				openOptions();
				break;
			case Exit:
				System.exit(0);	

		}
	}


	private void setMainViewAccess(Operator operator) {

		// по умолчанию все элементы неактивны
		for (MainViewCommand command: MainViewCommand.values()) {
			mainView.getActionByCommand(command).setEnabled(false);
		}

		// Элемент Закрыть всегда активен
		mainView.getActionByCommand(MainViewCommand.Exit).setEnabled(true);
		mainView.getActionByCommand(MainViewCommand.Options).setEnabled(true);

		
		if (operator != null) { // оператор назначен

			mainView.getActionByCommand(MainViewCommand.Logout).setEnabled(true);
			mainView.getActionByCommand(MainViewCommand.NewReception).setEnabled(true);
			mainView.getActionByCommand(MainViewCommand.UpdateReceptions).setEnabled(true);
			
			if (operator.isControler()) { // для контролера
				mainView.getActionByCommand(MainViewCommand.ReceptionStatuses).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.OpenReceptionsReestr).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.OpenStatistic).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.FLs).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.ULs).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.RealtyObjects).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.RealtyObjectTypes).setEnabled(true);
			}

			if (operator.isAdmin()) { // для администратора
				mainView.getActionByCommand(MainViewCommand.ReceptionStatuses).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.OpenReceptionsReestr).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.OpenStatistic).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.FLs).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.ULs).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.RealtyObjects).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.RealtyObjectTypes).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.Services).setEnabled(true);
				mainView.getActionByCommand(MainViewCommand.Operators).setEnabled(true);
				
			}

		} else { // если оператор не назначен
			mainView.getActionByCommand(MainViewCommand.Login).setEnabled(true);			
		}

	}



	private void addReception(Reception reception) {
		receptionsModel.addReception(reception);
	}

	private void readReceptions() {
		processDialog = new ProcessDialog(mainView,"Работаю", "Обновляю список приемов");
		// Читаем список опреаторов в отдельном потоке
		Thread receptionsReaderThread = new Thread(new ReceptionsReader());
		receptionsReaderThread.start();
		// открываем модальное окно  - ожидаем его закрытия (закрывается при оповещении от модели о завершении)
		processDialog.setVisible(true);
		List<Reception> receptions = receptionsModel.getAllReceptions();
		mainView.getTableModel().setReceptions(receptions);
		// Освобождаем ресурсы
		processDialog.dispose();
		processDialog = null;
	}

	class ReceptionsReader implements Runnable {
		public void run() {
			receptionsModel.readReceptionsFromDB();
		}
	}

	@Override 
	public void receptionsModelStateChanged(ReceptionsModelState state) {
		if (processDialog == null) return; // работаем только при существующем диалоге
		switch (state) {
			case UPDATED:
				processDialog.setVisible(false);
			break;
			
			case CONNECTING:
				processDialog.setContent("Соединяюсь...");
			break;

			case READING:
				processDialog.setContent("Получаю список приемов");
			break;	
			
			case ERROR:
				
				processDialog.setContent("Ошибка");
				processDialog.setResult(ProcessDialog.ERROR);
				processDialog.setVisible(false);
				
			break;		

		}
	}

	private void login() {
		// создаем диалог отображения процесса получения списка операторов
		processDialog = new ProcessDialog(mainView, "Работаю", "Читаю список операторов");
		// читаем операторов в отдельном потоке
		Thread operatorsReaderThread = new Thread(new OperatorsReader());
		operatorsReaderThread.start();
		// открываем окно с процессом выполнения - окно модальное, ожидаем закрытия
		processDialog.setVisible(true);	

		if (processDialog.getResult() == ProcessDialog.NONE) {
			//получаем список действующих операторов
			List<Operator> operators = operatorsModel.getActiveOperators();
			// создаем диалог ввода пароля
			
			loginDialog = new LoginDialog(mainView, operators);
			loginDialog.showDialog();

			// делаем проверку на итог - назначаем оператора
			if (loginDialog.getResult() == LoginDialog.OK) {
				Operator operator = loginDialog.getOperator();
				CurrentOperator.getInstance().setOperator(operator);
				new InfoDialog(mainView, "<html>Добро пожаловать, <p><i>" + 
											operator.getFirstName() + 
											" " + operator.getParentName() + "</i>!</p></html>", 1000, InfoDialog.STAR).showInfo();
			} else {
				CurrentOperator.getInstance().resetOperator();
				HibernateUtil.closeConnection();
			}
			// Освобождаем ресурсы
			loginDialog.dispose();
			loginDialog = null;

			
		} else  if (processDialog.getResult() == ProcessDialog.ERROR) {
			JOptionPane.showMessageDialog(mainView, "Ошибка при подключении к базе данных", "Ошибка", JOptionPane.ERROR_MESSAGE);
		} else if (processDialog.getResult() == ProcessDialog.CANCEL) {
			/// действия при отмене чтения  - пока ничего не делаем
		}

		processDialog.dispose();
		processDialog = null;

	}

	public void operatorsModelStateChanged(OperatorsModelState state) {
		if (processDialog == null) return; // Если диалог процесса пуст, нам нечего здесь делать
		switch (state) {
			case CONNECTING:
				processDialog.setContent("Соединяюсь...");
				break;
			case READING:
				processDialog.setContent("Читаю список операторов из базы...");
				break;
			case UPDATED:
				processDialog.setVisible(false);
				break;
			case ERROR:
				processDialog.setResult(ProcessDialog.ERROR);
				// JOptionPane.showMessageDialog(mainView, "Ошибка", "Ошибка при подключении к базе данных", JOptionPane.ERROR_MESSAGE);
				break;	
			
		}
	}


	class OperatorsReader implements Runnable {
		public void run() {
			operatorsModel.readOperators();
		}
	}


	private void logout() {
		CurrentOperator.getInstance().resetOperator();
		HibernateUtil.closeConnection();
	}

	private void openMakeReceptionView() {
		MakeReceptionViewController.getInstance().openView();
	}

	private void openServicesView() {
		ServicesDialogController.getInstance().openEditDialog(mainView);
	}

	private void openStatistic() {
		StatisticViewController.getInstance().openView();
	}

	private void openOperators() {
		OperatorListDialogController.getInstance().showDialog(mainView);		
	}

	private void openReceptionStatuses() {
		ReceptionStatusListDialogController.getInstance().openDialog(mainView);
	}

	private void openFLs() {
		FLListDialogController.getInstance().openDialog(mainView);
	}

	private void openULs() {
		ULListDialogController.getInstance().openDialog(mainView);
	}

	private void openReceptionsReestr() {
		ReestrViewController.getInstance().openView();
	}

	private void openOptions() {
		OptionsDialog optionsDialog = new OptionsDialog(mainView);
		optionsDialog.showDialog();
	}

	private void openRealtyObjects() {
		RealtyObjectListDialogController.getInstance().showDialog(mainView);
	}

	private void openRealtyObjectTypes() {
		RealtyTypeListDialogController.getInstance().showDialog(mainView);
	}


	/**
	* Обрабатывет событие смены оператора
	*/
	@Override
	public void currentOperatorChanged(Operator operator) {

		if (operator != null) {
			mainView.setCurrentOperatorLabel(operator.getFIO());	
		} else {
			mainView.setCurrentOperatorLabel("");
		}

		setMainViewAccess(operator);
	}

}
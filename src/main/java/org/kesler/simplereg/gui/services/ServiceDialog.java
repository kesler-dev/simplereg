package org.kesler.simplereg.gui.services;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import net.miginfocom.swing.MigLayout;

import org.kesler.simplereg.gui.pvd.type.PVDPackageTypeDialogController;
import org.kesler.simplereg.logic.Service;
import org.kesler.simplereg.util.ResourcesUtil;

public class ServiceDialog extends JDialog {

	public static int NONE = -1;
	public static int OK = 0;
	public static int CANCEL = 1;

	private JDialog parentDialog;
	private int result;

//    private String separator = System.lineSeparator();

	private Service service;

    private JTextField codeTextField;
	private JTextArea nameTextArea;
    private JTextArea pvdTypesTextArea;
	private JCheckBox enabledCheckBox;

	public ServiceDialog(JDialog parentDialog) {
		super(parentDialog,"Создать", true);
		this.parentDialog = parentDialog;
		result = NONE;

		service = new Service();
		//service.setName("Новая услуга");
		service.setEnabled(true);
		createGUI();
	}

	public ServiceDialog(JDialog parentDialog, Service service) {
		super(parentDialog,"Изменить",true);
		this.parentDialog = parentDialog;
		result = NONE;

		this.service = service;
		createGUI();
	}

	public int getResult() {
		return result;
	}

	public Service getService() {
		return service;
	}

	private void createGUI() {

		// Основная панель
		JPanel mainPanel = new JPanel(new BorderLayout());

		// Панель данных
		JPanel dataPanel = new JPanel(new MigLayout("fill"));

        codeTextField = new JTextField(10);

		nameTextArea = new JTextArea();
		nameTextArea.setLineWrap(true);
		nameTextArea.setWrapStyleWord(true);
		JScrollPane nameTextAreaScrollPane = new JScrollPane(nameTextArea);
		//nameTextAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        pvdTypesTextArea = new JTextArea();
        JScrollPane pvdTypesTextAreaScrollPane = new JScrollPane(pvdTypesTextArea);

        JButton selectPVDTypesButton = new JButton("...");
        selectPVDTypesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPVDTypes();
            }
        });

		enabledCheckBox = new JCheckBox("Действующая");

        // Собираем панель данных
        dataPanel.add(new JLabel("Номер соглашения"), "span, split 2");
        dataPanel.add(codeTextField, "wrap");
		dataPanel.add(new JLabel("Наименование: "), "wrap");
		dataPanel.add(nameTextAreaScrollPane, "span, grow");
        dataPanel.add(new JLabel("Коды типов ПК ПВД"), "wrap");
        dataPanel.add(pvdTypesTextAreaScrollPane, "span, split 2, pushx, grow");
        dataPanel.add(selectPVDTypesButton);
		dataPanel.add(enabledCheckBox);

		// Панель кнопок
		JPanel buttonPanel = new JPanel();

		JButton okButton = new JButton("Ok");
		okButton.setIcon(ResourcesUtil.getIcon("accept.png"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (saveServiceFromGUI()) { // если удалось сохранить услугу
					result = OK;
					setVisible(false);					
				} 
			}
		});

		JButton cancelButton = new JButton("Отмена");
		cancelButton.setIcon(ResourcesUtil.getIcon("cancel.png"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				result = CANCEL;
				setVisible(false);
			}
		});

		// собираем панель кнопок

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		// собираем главную панель
		mainPanel.add(dataPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		this.setContentPane(mainPanel);
		this.setSize(500, 300);
		this.setLocationRelativeTo(parentDialog);

		loadGUIFromService();

		nameTextArea.requestFocus();

	}

	private void loadGUIFromService() {

        String code = service.getCode();
        if(code == null) code = "";

        codeTextField.setText(code);

        String name = service.getName();
		if (name == null) name = "";

		nameTextArea.setText(name);

		Boolean enabled = service.getEnabled();
		if (enabled == null) enabled = false;

        String typeIDsString = service.getPkpvdTypeIDs();
        pvdTypesTextArea.setText(typeIDsString.replace(",","\n"));

		enabledCheckBox.setSelected(enabled);
	}

	private boolean saveServiceFromGUI() {

        String code = codeTextField.getText();
        service.setCode(code);

		String name = nameTextArea.getText();
		if (!name.isEmpty()) {
			service.setName(name);
		} else {
			JOptionPane.showMessageDialog(this, "Наименование услуги не может быть пустым", "Ошибка", JOptionPane.ERROR_MESSAGE);
			return false;
		}

        String typeIDString = pvdTypesTextArea.getText().replaceAll("\n",",");
        service.setPkpvdTypeIDs(typeIDString);

		service.setEnabled(enabledCheckBox.isSelected());

		return true;
	}

    private void selectPVDTypes() {

        String typeIDsString = pvdTypesTextArea.getText().replaceAll("\n",",");
        typeIDsString = PVDPackageTypeDialogController.getInstance().showDialog(this, typeIDsString);

        pvdTypesTextArea.setText(typeIDsString.replaceAll(",","\n"));

    }

}

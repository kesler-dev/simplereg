package org.kesler.simplereg.gui.services;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.kesler.simplereg.util.ResourcesUtil;

import org.kesler.simplereg.logic.Service;

public class ServicesDialog extends JDialog{
	public static final int SELECT = 0;
	public static final int EDIT = 1;

	private ServicesDialogController controller;
	private JTree servicesTree;

	private DefaultMutableTreeNode selectedNode = null;
	private Service selectedService = null;


	public ServicesDialog(JFrame frame, ServicesDialogController controller, int mode) {
		super(frame, true);
		this.controller = controller;
		switch(mode) {
			case SELECT:
				this.setContentPane(createSelectContentPane());
				break;
			case EDIT:
				this.setContentPane(createEditContentPane());
				break;
			default :
				this.setContentPane(createSelectContentPane());
		}
		
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);

	}


	private JPanel createEditContentPane() {
		// Основная панель
		JPanel mainPanel = new JPanel(new BorderLayout());

		JPanel treePanel = createEditTreePanel();

		JPanel buttonPanel = new JPanel();

		JButton updateButton = new JButton("Обновить");
		updateButton.setIcon(ResourcesUtil.getIcon("arrow_refresh.png"));
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				TreePath selectionPath = servicesTree.getSelectionModel().getSelectionPath();
				controller.reloadTree();
				if (selectionPath != null) {
					servicesTree.getSelectionModel().setSelectionPath(selectionPath);
					servicesTree.makeVisible(selectionPath);
				}
			}
		});

		JButton okButton = new JButton("Ok");
		okButton.setIcon(ResourcesUtil.getIcon("accept.png"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
			}
		});

		JButton cancelButton = new JButton("Отменить");
		cancelButton.setIcon(ResourcesUtil.getIcon("cancel.png"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
			}
		});


		buttonPanel.add(updateButton);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		mainPanel.add(BorderLayout.CENTER, treePanel);
		mainPanel.add(BorderLayout.SOUTH, buttonPanel);

		return mainPanel;

	}

	private JPanel createSelectContentPane() {
		JPanel mainPanel = new JPanel(new BorderLayout());

		JPanel treePanel = createSelectTreePanel();

		// панель кнопок
		JPanel buttonPanel = new JPanel();

		JButton selectButton = new JButton("Выбрать");
		selectButton.setIcon(ResourcesUtil.getIcon("accept.png"));
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (selectedService == null) {
					JOptionPane.showMessageDialog(null,
                				"Услуга не выбрана",
                				"Ошибка",
                				JOptionPane.ERROR_MESSAGE);
				} else {
					setVisible(false);
				}
			}
		});

		JButton cancelButton = new JButton("Отменить");
		cancelButton.setIcon(ResourcesUtil.getIcon("cancel.png"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
			}
		});

		// собираем панель кнопок
		buttonPanel.add(selectButton);
		buttonPanel.add(cancelButton);

		mainPanel.add(BorderLayout.CENTER, treePanel);
		mainPanel.add(BorderLayout.SOUTH, buttonPanel);

		return mainPanel;

	}

	// создает панель с возможностью выбора услуги без возможности редактирования
	private JPanel createSelectTreePanel() {
		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Услуги");
		servicesTree = new JTree(rootNode);
		servicesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		servicesTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent ev) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) servicesTree.getLastSelectedPathComponent();
				if (node == null) return;
				if (node.isRoot()) return ;

				selectedService = (Service)node.getUserObject();
			}
		});

		JScrollPane servicesScrollPane = new JScrollPane(servicesTree);

		treePanel.add(BorderLayout.CENTER, servicesScrollPane);


		return treePanel;		
	}

	// создает панель дерева с возможностью редактирования
	private JPanel createEditTreePanel() {
		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Услуги");
		servicesTree = new JTree(rootNode);
		servicesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		servicesTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent ev) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) servicesTree.getLastSelectedPathComponent();
				if (node == null) return;
				if (node.isRoot()) return ;

				selectedNode = node;
				selectedService = (Service)node.getUserObject();
				System.out.println("Selected service: " + selectedService);
			}
		});

		JScrollPane servicesScrollPane = new JScrollPane(servicesTree);

		// панель кнопок управления деревом
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		final JButton addButton = new JButton();
		addButton.setIcon(ResourcesUtil.getIcon("add.png"));

		final JPopupMenu addServicePopupMenu = new JPopupMenu();
		// пункт меню добавления услуги на том же уровне
		JMenuItem addServiceMenuItem = new JMenuItem("Добавить услугу на том же уровне");
		addServiceMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (selectedNode.isRoot()) return; /// Добавить вывод сообщения о том, что нельзя добавить
				controller.addSubService((DefaultMutableTreeNode)selectedNode.getParent());
			}
		});
		// пункт меню добавления подуслуги
		JMenuItem addSubServiceMenuItem = new JMenuItem("Добавить подуслугу");
		addSubServiceMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				controller.addSubService(selectedNode);
			}
		});

		addServicePopupMenu.add(addServiceMenuItem);
		addServicePopupMenu.add(addSubServiceMenuItem);
		// кнопка для вызова вариантов добавления услуги
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				addServicePopupMenu.show(addButton, addButton.getWidth(), 0);
			}
		});

		JButton editButton = new JButton();
		editButton.setIcon(ResourcesUtil.getIcon("pencil.png"));
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (selectedService != null) {
					controller.editService(selectedNode);
				} else {
					JOptionPane.showMessageDialog(null, "Услуга не выбрана", "Ошибка", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});

		JButton removeButton = new JButton();
		removeButton.setIcon(ResourcesUtil.getIcon("delete.png"));
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (selectedService != null) {
					controller.removeService(selectedNode);
				} else {
					JOptionPane.showMessageDialog(null, "Услуга не выбрана", "Ошибка", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});

		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(removeButton);


		treePanel.add(servicesScrollPane, BorderLayout.CENTER);
		treePanel.add(buttonPanel, BorderLayout.SOUTH);

		return treePanel;		
	}

	/**
	* Возвращает модель дерева услуг, привязанную к виду 
	*/
	public TreeModel getTreeModel() {
		return servicesTree.getModel();
	}

	/**
	* Возвращает выбранную услугу
	*/
	public Service getSelectedService() {
		return selectedService;
	}

}
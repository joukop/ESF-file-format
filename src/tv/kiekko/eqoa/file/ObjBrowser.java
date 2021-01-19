package tv.kiekko.eqoa.file;

import java.awt.image.BufferedImage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/* A tool for viewing the object hierarchy. Dependency: JavaFX */

public class ObjBrowser extends Application {
	static String tunariaPath = "Tunaria.esf";

	public static void main(String[] args) {
		if (args.length > 0)
			tunariaPath = args[0];
		launch(args);
	}

	class ObjNodeItem extends TreeItem<ObjInfo> {

		ObjInfo obj;

		public ObjNodeItem(ObjInfo o) {
			super(o);
			this.obj = o;
			for (ObjInfo c : o.children) {
				this.getChildren().add(new ObjNodeItem(c));
			}
		}

		@Override
		public String toString() {
			return obj.toString();
		}

	}

	ObjFile file = null;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("ObjBrowser");

		TreeView<ObjInfo> list = new TreeView<>();

		ImageView iv = new ImageView();

		list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ObjInfo>>() {

			@Override
			public void changed(ObservableValue<? extends TreeItem<ObjInfo>> observable, TreeItem<ObjInfo> oldValue,
					TreeItem<ObjInfo> newValue) {

				ObjInfo info = newValue.getValue();

				if (info != null && ObjFile.getObjectName(info.type).startsWith("Surface(")) {

					try {
						Surface surface = (Surface) info.getObj();

						Platform.runLater(() -> {

							BufferedImage image = surface.getTexture();

							iv.setImage(SwingFXUtils.toFXImage(image, null));

						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		});

		BorderPane rootPane = new BorderPane();
		rootPane.setCenter(new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS));

		TableView<ObjInfo> table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<ObjInfo, String> column1 = new TableColumn<>("ID");
		column1.setCellValueFactory((param) -> {
			if (param.getValue() != null)
				return new SimpleStringProperty(String.format("%08x", param.getValue().dictID));
			else
				return new SimpleStringProperty("-");
		});

		TableColumn<ObjInfo, String> column2 = new TableColumn<>("Object");
		column2.setCellValueFactory((param) -> {
			if (param.getValue() != null)
				return new SimpleStringProperty(param.getValue().toString());
			else
				return new SimpleStringProperty("-");
		});

		table.getColumns().add(column1);
		table.getColumns().add(column2);

		new Thread(() -> {
			try {
				file = new ObjFile(tunariaPath);
				ObjInfo root = file.getRoot();

				Platform.runLater(() -> {
					rootPane.setCenter(list);
					list.setRoot(new ObjNodeItem(root));
					// table.setItems(FXCollections.observableArrayList(root.getDictionary().values()));
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		TabPane tabs = new TabPane();

		/*
		 * HBox box = new HBox(); Button export = new Button("Export");
		 * export.setOnAction((e) -> {
		 * 
		 * }); box.getChildren().add(export); rootPane.setTop(box);
		 */
		rootPane.setRight(iv);

		tabs.getTabs().add(new Tab("ObjFile", rootPane));
		// tabs.getTabs().add(new Tab("Dictionary", table));

		primaryStage.setScene(new Scene(tabs, 300, 250));
		primaryStage.show();
	}
}

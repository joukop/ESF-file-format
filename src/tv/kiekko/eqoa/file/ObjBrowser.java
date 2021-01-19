package tv.kiekko.eqoa.file;

import java.awt.image.BufferedImage;
import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ObjBrowser extends Application {

	public static void main(String[] args) {
		if (args.length > 0) objFileName = args[0];
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

	static String objFileName = null;
	
	void loadFile(File f) {

		list.setRoot(null);
		
		new Thread(() -> {
			try {
				file = new ObjFile(f);
				ObjInfo root = file.getRoot();

				Platform.runLater(() -> {
					rootPane.setCenter(list);
					list.setRoot(new ObjNodeItem(root));
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

	}

	TreeView<ObjInfo> list;
	
	BorderPane rootPane;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("ObjBrowser");

		list = new TreeView<>();

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

		rootPane = new BorderPane();
		ProgressIndicator ind = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
		ind.setMaxSize(100, 100);
		rootPane.setCenter(ind);

		rootPane.setRight(iv);

		primaryStage.setScene(new Scene(rootPane, 400, 600));
		primaryStage.show();

		FileChooser fileChooser = new FileChooser();
		if (objFileName == null) {
			File selectedFile = fileChooser.showOpenDialog(primaryStage);
			loadFile(selectedFile);
		} else {
			loadFile(new File(objFileName));
		}
	}
}

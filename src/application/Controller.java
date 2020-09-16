package application;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {

	@FXML
	private Label dateTimeLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setDateAndTime();

	}

	private void setDateAndTime() {
		Thread timerThread = new Thread(() -> {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE | dd/MM/yyyy HH:mm:ss");

			while (true) {
				try {
					Thread.sleep(1000); // 1 second

				} catch (InterruptedException e) {
					e.printStackTrace();

				}
				final String time = simpleDateFormat.format(new Date());
				Platform.runLater(() -> {
					dateTimeLabel.setText(time);

				});
			}
		});
		// Start the thread
		timerThread.start();
	}

}
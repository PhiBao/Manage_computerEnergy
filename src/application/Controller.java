package application;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import BatterySystem.PowerSource;

public class Controller implements Initializable {

	@FXML
	private Label dateTimeLabel;

	@FXML
	private Label powerLabel;

	@FXML
	private Label batteryLabel;

	@FXML
	private Slider sliderBrightness;

	@FXML
	private ComboBox<String> optionShutdown;

	@FXML
	private Button performButton;

	@FXML
	private TextField minutesText;

	@FXML
	private TextField hoursText;

	static String commandLine = "";
	ObservableList<String> list = FXCollections.observableArrayList("Hibernate", "Shutdown", "Restart");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		optionShutdown.setItems(list);
		performButton.setDisable(true);
		hoursText.setText("0");
		minutesText.setText("0");

		sliderBrightness.setValue(60);
		sliderBrightness.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
				try {
					BrightnessManager.setBrightness(Math.round(newValue.intValue()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		setDateAndTime();
		setBatterySystem();
	}

	public void optionChanged(ActionEvent event) {
		if (optionShutdown.getValue() == "Shutdown")
			commandLine = "shutdown -s";
		if (optionShutdown.getValue() == "Hibernate")
			commandLine = "shutdown -h";
		if (optionShutdown.getValue() == "Restart")
			commandLine = "shutdown -r";
		performButton.setDisable(false);
	}

	public void performShutdown(ActionEvent event) throws NumberFormatException, InterruptedException, IOException {
		shutdownThread();
		/*
		 * String hours = hoursText.getText(); String minutes = minutesText.getText();
		 * Thread.sleep(Integer.parseInt(hours) * 60 * 60 * 1000 +
		 * Integer.parseInt(minutes) * 60 * 1000);
		 * Runtime.getRuntime().exec(commandLine);
		 */
	}

	private void shutdownThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String hours = hoursText.getText();
				String minutes = minutesText.getText();

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						String message = "Your command will be executed after " + hours + " hours " + minutes
								+ " minutes!";
						Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setTitle("Information");
						alert.setHeaderText("Notification");
						alert.setContentText(message);
						alert.show();
					}
				});

				try {
					Thread.sleep(Integer.parseInt(hours) * 60 * 60 * 1000 + Integer.parseInt(minutes) * 60 * 1000);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					@SuppressWarnings("unused")
					Process shutdownProcess = Runtime.getRuntime().exec(commandLine);
					System.exit(0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void setBatterySystem() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					StringBuilder sb = new StringBuilder("Power: ");

					if (PowerSource.getPowerSources().length == 0) {
						sb.append("Unknown");
					} else {
						double timeRemaining = PowerSource.getPowerSources()[0].getTimeRemaining();

						if (timeRemaining < -1d)
							sb.append("Charging");
						else if (timeRemaining < 0d)
							sb.append("Calculating time remaining");
						else
							sb.append(String.format("%d:%02d' remaining", (int) (timeRemaining / 3600),
									(int) (timeRemaining / 60) % 60));
					}

					for (PowerSource pSource : PowerSource.getPowerSources()) {
						sb.append(String.format("%n %s @ %.1f%%", pSource.getName(),
								pSource.getRemainingCapacity() * 100d));
					}
					final String tmp[] = sb.toString().split("\r");

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							powerLabel.setText(tmp[0].trim());
							batteryLabel.setText(tmp[1].trim());
						}
					});

					try {
						Thread.sleep(30000); // 30s

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void setDateAndTime() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE | dd/MM/yyyy HH:mm:ss");

				while (true) {
					final String time = simpleDateFormat.format(new Date());

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							dateTimeLabel.setText(time);
						}
					});
					try {
						Thread.sleep(1000); // 1 second
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TimerController {

    private int seconds;
    private Timeline timeline;
    private Label timerLabel;

    public TimerController(Label label, int totalSeconds, Runnable onTimeOut) {
        this.timerLabel = label;
        this.seconds = totalSeconds;

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds--;
            updateLabel();
            if (seconds <= 0) {
                timeline.stop();
                timerLabel.setText("00:00");
                if (onTimeOut != null) onTimeOut.run();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        updateLabel();
    }

    public void start() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }

    public void reset(int totalSeconds) {
        timeline.stop();
        this.seconds = totalSeconds;
        updateLabel();
    }

    private void updateLabel() {
        int min = seconds / 60;
        int sec = seconds % 60;
        timerLabel.setText(String.format("%02d:%02d", min, sec));
    }
}
package UserInterface;

import javax.swing.*;

public class MainWindow extends JFrame {
	private String sessionUser;
	
    public MainWindow() {
        setTitle("Personal Finance Tracker");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // show login form
        add(new HomePage(this));

        setVisible(true);
    }
    
    public void setSession(String email) {
        this.sessionUser = email;
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DownloadUI {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Downloader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 250);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        JTextField urlField = createPlaceholderField("Url");
        panel.add(urlField, c);

        c.gridwidth = 1;
        c.gridy = 1;
        JTextField requestNameField = createPlaceholderField("Request Name");
        panel.add(requestNameField, c);

        c.gridx = 1;
        JTextField saveNameField = createPlaceholderField("Save Name");
        panel.add(saveNameField, c);

        c.gridx = 2;
        JTextField endOffsetField = createPlaceholderField("Chunk Size");
        panel.add(endOffsetField, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        JTextField savePathField = createPlaceholderField("Save Path");
        savePathField.setEditable(false);
        panel.add(savePathField, c);

        c.gridx = 2;
        c.gridwidth = 1;
        JButton browseBtn = new JButton("browse");
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int res = chooser.showOpenDialog(frame);
            if (res == JFileChooser.APPROVE_OPTION) {
                savePathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        panel.add(browseBtn, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 3;
        JButton downloadBtn = new JButton("download");
        downloadBtn.addActionListener(e ->{
            String url = urlField.getText();
            String requestName = requestNameField.getText();
            String savePath = savePathField.getText();
            String saveName = saveNameField.getText();
            int chunkSize = Integer.parseInt(endOffsetField.getText());

            // ToDo UI 수정
            // ToDo Multiplexing 방식으로 수정
//            new MultiThreadDownloader(url, requestName, savePath, saveName, chunkSize)
//                    .download();

            new JToast(frame,"complete!",1000);
        });
        panel.add(downloadBtn, c);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static JTextField createPlaceholderField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (field.getForeground().equals(Color.GRAY)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
        });

        return field;
    }
}

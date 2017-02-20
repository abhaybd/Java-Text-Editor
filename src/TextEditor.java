import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TextEditor extends JFrame implements KeyListener{
	private static final long serialVersionUID = 1L;

	public static void main(String[] args){
		new TextEditor("untitled.txt*");
	}
	
	private JTextArea area;
	public  File saved = null;
	
	public TextEditor(String title){
		this.setTitle(title);
		area = new JTextArea();
		init();
	}
	
	public TextEditor(String title, JTextArea area){
		this.setTitle(title);
		this.area = area;
		init();
	}
	
	private void init(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setSize(512, 378);
		area.addKeyListener(this);
		JScrollPane scroll = new JScrollPane(area);
		getContentPane().add(scroll);
		//pack();
		setVisible(true);
		setUpDrop();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	private void setUpDrop(){
		new FileDrop(this, new FileDrop.Listener() {
			@Override
			public void filesDropped(File[] files) {
				if(files.length == 1){
					load(files[0]);
				}
				else{
					for(File file:files){
						try(Scanner scanner = new Scanner(file)){
							StringBuilder builder = new StringBuilder();
							while(scanner.hasNextLine()){
								builder.append(scanner.nextLine() + "\n");
							}
							TextEditor t = new TextEditor(file.getName(), new JTextArea(builder.toString()));
							t.saved = file;
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}					
				}
			}
		});
	}
	
	private void openFile(){
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
	    chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	File file = chooser.getSelectedFile();
	        load(file);
	    }
	}
	
	private void load(File file){
		try (Scanner scanner = new Scanner(file)){
			StringBuilder builder = new StringBuilder();
			while(scanner.hasNextLine()){
				builder.append(scanner.nextLine() + "\n");
			}
			area.setText(builder.toString());
			this.setTitle(file.getName());
			saved = file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void saveFile(){
		if(saved != null){
			write(saved);
		}
		else{
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if(chooser.getFileFilter() == filter && !file.getName().endsWith(".txt")) file = new File(chooser.getSelectedFile() + ".txt");
				write(file);
				this.setTitle(file.getName());
				saved = file;
			}
		}
		if(this.getTitle().endsWith("*")) {
			System.out.println(this.getTitle());
			String newTitle = this.getTitle().substring(0, this.getTitle().length()-1); 
			System.out.println(newTitle);
			this.setTitle(newTitle);
			this.revalidate();
		}
	}
	
	private void write(File file){
		try(PrintWriter out = new PrintWriter(file)){
			for(String s:area.getText().split("\\n")){
				out.println(s);
			}
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private boolean control = false;

	@Override
	public void keyPressed(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_CONTROL) control = true;
		if(control){
			if(key.getKeyCode() == KeyEvent.VK_O){
				openFile();
				control = false;
			}
			else if(key.getKeyCode() == KeyEvent.VK_S){
				saveFile();
				control = false;
			}
			else if(key.getKeyCode() == KeyEvent.VK_N){
				new TextEditor("untitled.txt*");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_CONTROL) {
			control = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent key) {
		if(!this.getTitle().endsWith("*")) this.setTitle(this.getTitle() + "*");
	}
}

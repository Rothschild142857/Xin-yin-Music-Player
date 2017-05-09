package mp3;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
public class player extends Frame {
    boolean isStop = true;// 控制播放线程
    boolean hasStop = true;// 播放线程状态
    //final String happyfilepath = "/Users/yangyuxuan/music/网易云音乐/";
    String filepath;// 播放文件目录
    String filename;// 播放文件名称
    String nextfilename;
    String nextfilepath;
    AudioInputStream audioInputStream;// 文件流
    AudioFormat audioFormat;// 文件格式
    SourceDataLine sourceDataLine;// 输出设备
    
    int n;//音乐序号
    int mood[] = new int[20];//情绪信号队列
    
    List list;// 文件列表
    Label labelfilepath;//播放目录显示标签
    Label labelfilename;//播放文件显示标签
    Label labelmusicmood;//播放音乐的情绪
 
    final String happypath = "/Users/yangyuxuan/Music/happy/";
    final String calmpath = "/Users/yangyuxuan/Music/calm/";
    final String sadpath = "/Users/yangyuxuan/Music/sad/";
    
    public player() {
        // 设置窗体属性
        setLayout(new BorderLayout());
        setTitle("心音播放器");
        setSize(350, 100);
 
        // 建立菜单栏
//        MenuBar menubar = new MenuBar();
//        Menu menufile = new Menu("文件");
//        MenuItem menuopen = new MenuItem("打开", new MenuShortcut(KeyEvent.VK_O));
//        menufile.add(menuopen);
//        menufile.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                open();
//            }
//        });
//        menubar.add(menufile);
//        setMenuBar(menubar);
       
        // 文件列表
//        list = new List(10);
//        list.addMouseListener(new MouseAdapter() {
//            public void mouseClicked(MouseEvent e) {
//                // 双击时处理
//                if (e.getClickCount() == 2) {
//                    // 播放选中的文件
//                    filename = list.getSelectedItem();
//                    play();
//                }
//            }
//        });
//        add(list, "Center");
        
        
        // 信息显示
        Panel panel = new Panel(new GridLayout(3, 1));
        labelfilepath = new Label("播放目录：");
        labelfilename = new Label("播放文件：");
        labelmusicmood = new Label("当前情绪：");
        panel.add(labelfilepath);
        panel.add(labelfilename);
        panel.add(labelmusicmood);
        add(panel, "North");
        
        init();
        
        //播放控制按钮
        Panel ButtonPanel = new Panel(new GridLayout(1,3));
        Button Bplay = new Button("播放");
        Bplay.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		playClick(e);	
        	}
        });
        Button BStop = new Button("停止");
        BStop.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		stopClick(e);
        	}
        });
        Button Bnext = new Button("下一首");
        Bnext.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		nextClick(e);
        	}
        });
        ButtonPanel.add(Bplay);
        ButtonPanel.add(Bnext);
        ButtonPanel.add(BStop);
        
        add(ButtonPanel,"South");
        
        
        // 注册窗体关闭事件
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setVisible(true);
    }
 
    void playClick(ActionEvent e){
        play();
//        try {
//			Thread.sleep(10);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
//        while(hasStop){
//        	next();
//        	play();
//        }
    }
    void stopClick(ActionEvent e){
    	isStop = true;
    }
    void nextClick(ActionEvent e){
    	isStop = true;
    	next();
    	play();
    }
    private void next(){
    	File filedir = new File( nextfilepath);
    	File[] filelist = filedir.listFiles();
    	if(filepath == nextfilepath)
    	{
    		n=(n+1)%3;
    	}
    	else
    	{
    		Random r = new Random();
    		n = r.nextInt(3);
    		n = Math.abs(r.nextInt()%3);
    	}
    	filename = filelist[n].getName().toLowerCase();
    	filepath = nextfilepath;
 
    }
    // 打开
    private void init() {

    	filepath = calmpath;

        if (filepath != null) {
           //labelfilepath.setText("播放目录：" + filepath);

            File filedir = new File(filepath);
            File[] filelist = filedir.listFiles();
            Random r = new Random();
            n =r.nextInt(3);
			n= Math.abs(r.nextInt()%3);
			filename = filelist[n].getName().toLowerCase();
			nextfilepath = filepath;
			n = (n+1)%3;
			nextfilename = filelist[n].getName().toLowerCase();
			
			Thread moodRecvThread = new Thread(new moodRecvThread());
            moodRecvThread.start();
        }
    }
    // 播放
     private void play() {
        try {
            isStop = true;// 停止播放线程
            // 等待播放线程停止
            System.out.print("开始播放：" + filename);
            while (!hasStop) {
                System.out.print(".");
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                }
            }
            System.out.println("");
            File file = new File(filepath + filename);
            labelfilepath.setText("播放目录：" + filepath);
            labelfilename.setText("播放文件：" + filename);
 
            // 取得文件输入流
            audioInputStream = AudioSystem.getAudioInputStream(file);
            audioFormat = audioInputStream.getFormat();
            // 转换mp3文件编码
            if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        audioFormat.getSampleRate(), 16, audioFormat
                                .getChannels(), audioFormat.getChannels() * 2,
                        audioFormat.getSampleRate(), false);
                audioInputStream = AudioSystem.getAudioInputStream(audioFormat,
                        audioInputStream);
            }
           
            // 打开输出设备
            DataLine.Info dataLineInfo = new DataLine.Info(
                    SourceDataLine.class, audioFormat,
                    AudioSystem.NOT_SPECIFIED);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
 
            // 创建独立线程进行播放
            isStop = false;
            Thread playThread = new Thread(new PlayThread());
            playThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
 
    public static void main(String args[]) {
        new player();
    }
    
    class PlayThread extends Thread {
        byte tempBuffer[] = new byte[320];
     
        public void run() {
            try {
                int cnt;
                hasStop = false;
                // 读取数据到缓存数据
                while ((cnt = audioInputStream.read(tempBuffer, 0,
                        tempBuffer.length)) != -1) {
                    if (isStop)
                        break;
               
                    if (cnt > 0) {
                        // 写入缓存数据
                        sourceDataLine.write(tempBuffer, 0, cnt);
                    }
                }
                // Block等待临时数据被输出为空
                sourceDataLine.drain();
                sourceDataLine.close();
                hasStop = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
    
    class moodRecvThread extends Thread{
    	int t=0;
    	int data[] ={20,0,0};
    	int curmood =0;
    	//20个信号内不同情绪信号的个数，0 平静，1 快乐，2 悲伤
    	
    	public void run(){
    		try{
    			Scanner in = new Scanner(System.in);
    	    	while(true){
    	    		data[mood[t]]--;
    	    		mood[t] = in.nextInt();
    	    		if(mood[t]>=0&&mood[t]<3) data[mood[t]]++;
    	    		t=(t+1)%20;
    	    		for(int i=0;i<3;i++)
    	    		{
    	    			if(data[i]>data[curmood]) curmood =i;
    	    		}
    	    		switch(curmood){
    	    		case 0: nextfilepath = calmpath; labelmusicmood.setText("当前情绪：" +"平静" );break;
    	    		case 1: nextfilepath = happypath; labelmusicmood.setText("当前情绪：" +"愉快" );break;
    	    		case 2: nextfilepath = sadpath; labelmusicmood.setText("当前情绪：" +"悲伤" );break;
    	    		default: break;
    	    		}
    	    		
    	    	}
    		}
    		catch(Exception e){
    			e.printStackTrace();
    			System.exit(0);
    		}
    	}
    	
    }
}


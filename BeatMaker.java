import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.awt.Color;
import java.io.*;

public class BeatMaker {
	
	ArrayList<JCheckBox> listaPolWyboru;
	Sequencer sekwenser;
	Sequence sekwencja;
	Track sciezka;
	String[] nazwyInstrumentow = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat","Acoustic Snare","Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas","Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom","High Agogo", "Open Hi Conga"};
	int[] instrumenty = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63}; 
	private JFrame ramka;
	
	public static void main(String[] args) {
		BeatMaker gui = new BeatMaker();
		gui.tworzGUI();
	}
	
	
	public void tworzGUI() {
	
		ramka = new JFrame("BeatMaker");
		ramka.setVisible(true);
		ramka.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		BorderLayout uklad = new BorderLayout(); 
		JPanel pane = new JPanel(uklad);
		pane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
	
		listaPolWyboru = new ArrayList<JCheckBox>(); 
		Box obszarPrzyciskow = new Box(BoxLayout.Y_AXIS);
		Box obszarNazw = new Box(BoxLayout.Y_AXIS);
		for (int i=0; i<16; i++) {
			obszarNazw.add(new Label(nazwyInstrumentow[i]));
		}
		GridLayout siatkaPolWyboru = new GridLayout(16,16);
		siatkaPolWyboru.setVgap(1);
		siatkaPolWyboru.setHgap(2);
		JPanel panelGlowny = new JPanel(siatkaPolWyboru);
		for (int i = 0; i < 256; i++) {  //creating checkboxes and set as fasle, adding them to ArrayList and pane
			JCheckBox c = new JCheckBox();
			c.setSelected(false);
			listaPolWyboru.add(c);
			panelGlowny.add(c);
		}

	
		//Buttons
		JButton start = new JButton("Start");
		start.addActionListener(new startListener());
		JButton stop = new JButton("Stop");
		stop.addActionListener(new stopListener());
		JButton szybciej = new JButton("Szybciej");
		szybciej.addActionListener(new szybciejListener());
		JButton wolniej = new JButton("Wolniej");
		wolniej.addActionListener(new wolniejListener());
		JButton zapisz = new JButton("Zapisz");
		zapisz.addActionListener(new zapiszListener());
		JButton odtworz = new JButton("Wczytaj");
		odtworz.addActionListener(new odtworzListener());
		
		//adding to pane
		pane.add(BorderLayout.CENTER, panelGlowny);
		pane.add(BorderLayout.EAST, obszarPrzyciskow);
		pane.add(BorderLayout.WEST, obszarNazw);
		ramka.getContentPane().add(pane);
		//adding buttons 
		obszarPrzyciskow.add(start, BorderLayout.EAST);
		obszarPrzyciskow.add(stop, BorderLayout.EAST);
		obszarPrzyciskow.add(szybciej, BorderLayout.EAST);
		obszarPrzyciskow.add(wolniej, BorderLayout.EAST);
		obszarPrzyciskow.add(zapisz, BorderLayout.EAST);
		obszarPrzyciskow.add(odtworz, BorderLayout.EAST);
	
		
		konfigurujMidi();
		
		ramka.setBounds(50,50,300,300);
		ramka.pack();
		}
		
		
		public void konfigurujMidi() {  //Configuration of MIDI
			try {
				sekwenser = MidiSystem.getSequencer();
				sekwenser.open();
				sekwencja = new Sequence(Sequence.PPQ,4);
				sciezka = sekwencja.createTrack();
				sekwenser.setTempoInBPM(120);
			} catch(Exception e) {
			e.printStackTrace();
			}
		}
		
		public void tracks() { 
			int[] listaSciezki = null; //array- 16 tracks for each instrument
			sekwencja.deleteTrack(sciezka);
			sciezka = sekwencja.createTrack();
			for (int i = 0; i < 16; i++) {
				listaSciezki = new int[16];
				int klucz = instrumenty[i]; // which instrument should be used
				for (int j = 0; j < 16; j++) {
					JCheckBox jc = (JCheckBox) listaPolWyboru.get(j + (16*i));
						if ( jc.isSelected()) { //checking checkboxes to get know which instrument should play
							listaSciezki[j] = klucz;
							} else {
							listaSciezki[j] = 0;
							}
				}
			utworzSciezke(listaSciezki);
			sciezka.add(tworzZdarzenie(176,1,127,0,16));
			}
			
			sciezka.add(tworzZdarzenie(192,9,1,0,15));
			try {			
				sekwenser.setSequence(sekwencja);
				sekwenser.setLoopCount(sekwenser.LOOP_CONTINUOUSLY); //number of loops (infinity)
				sekwenser.start();
				sekwenser.setTempoInBPM(120);
				} catch(Exception e) { 
				e.printStackTrace();
				}
		}
		
	public void utworzSciezke(int[] lista) {
		for (int i = 0; i < 16; i++) {
			int klucz = lista[i];
			if (klucz != 0) {
				sciezka.add(tworzZdarzenie(144,9,klucz, 100, i));
				sciezka.add(tworzZdarzenie(128,9,klucz, 100, i+1));
			}
		}
		}
		
	public static MidiEvent tworzZdarzenie(int plc, int kanal, int jeden, int dwa, int takt) {
		MidiEvent zdarzenie = null;
		try {
			ShortMessage a = new ShortMessage();
			a.setMessage(plc, kanal, jeden, dwa);
			zdarzenie = new MidiEvent(a, takt);
		} catch(Exception e) { e.printStackTrace(); }
		return zdarzenie;
	} 
 
		
		
		class startListener implements ActionListener {
			public void actionPerformed(ActionEvent e){
				tracks();
			}	
		}
	
		class stopListener implements ActionListener {
			public void actionPerformed(ActionEvent e){
				sekwenser.stop();
			}	
		}
	
		class szybciejListener implements ActionListener {
			public void actionPerformed(ActionEvent e){
				float wspTempa = sekwenser.getTempoFactor();
				sekwenser.setTempoFactor((float)(wspTempa * 1.03));
			}	
		}
	
		class wolniejListener implements ActionListener {
			public void actionPerformed(ActionEvent e){
				float wspTempa = sekwenser.getTempoFactor();
				sekwenser.setTempoFactor((float)(wspTempa * .97)); 
		}
		}
		
	
		JFileChooser dialogFile = new JFileChooser();
		
		class zapiszListener implements ActionListener {
			public void actionPerformed(ActionEvent e){	
			dialogFile.showSaveDialog(ramka);
			zapiszPlik(dialogFile.getSelectedFile());
			}
		}
		private void zapiszPlik(File plik){
				boolean[] stanyPol = new boolean[256];
				for(int i = 0; i < 256; i++){
					JCheckBox pole = (JCheckBox) listaPolWyboru.get(i);
					if (pole.isSelected()){
						stanyPol[i] = true;
					}
				}
				try{
					FileOutputStream strumienPlk = new FileOutputStream(plik);
					ObjectOutputStream os = new ObjectOutputStream(strumienPlk);
					os.writeObject(stanyPol);
				}catch(Exception ex){
					ex.printStackTrace();
				}
		}
		
		
		class odtworzListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
			dialogFile.showOpenDialog(ramka);
			odtworzPlik(dialogFile.getSelectedFile());
			}
		}
		private void odtworzPlik(File plik){
		
			boolean[] stanyPol = null;
				try{
					FileInputStream plikDanych = new FileInputStream(plik);
					ObjectInputStream is = new ObjectInputStream(plikDanych);
					stanyPol = (boolean[]) is.readObject(); //readObject->object (array of bloolean from file)
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				for(int i = 0; i <256; i++){
					JCheckBox pole = (JCheckBox) listaPolWyboru.get(i);
					if(stanyPol[i]){
						pole.setSelected(true);
					} else {
						pole.setSelected(false);
					}
				}
				sekwenser.stop();
				tracks();
		}
		
	}




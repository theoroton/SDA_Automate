import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Automate {
	
	//Largeur des cellules (nombre impair)
	private static int TAILLE_LARGEUR = 101;
	//Hauteur des cellules
	private static int TAILLE_HAUTEUR = TAILLE_LARGEUR/2;
	
	//Cellules
	private int[][] cellules;
	
	//Ligne courante
	private int ligne_courante;
	
	//JFrame pour affichage
	private JFrame fenetre;
	//JPanel pour afficher l'automate
	private JPanel_Automate jpanel_automate;
	//JButton pour faire �voluer l'automate d'un pas
	private JButton evolution_automate;
	//JButton pour lancer compl�tement l'automate
	private JButton realisation_automate;

	private int[] regles = new int[8];

	private Boolean random = false;

	private double[] proba = new double[8];
	private Boolean probaB = false;
	
	
	/**
	 * Constructeur de l'automate
	 */
	public Automate(int[] regles_m, Boolean random_m, double[] proba_m, String nom) {
		Arrays.fill(regles, 0);
		if(regles_m.length > 0 ) {
			System.arraycopy(regles_m, 0, regles, 0, 8);
		}
		Arrays.fill(proba, 1.);
		if(proba_m.length > 0) {
			System.arraycopy(proba_m, 0, proba, 0, 8);
			probaB = true;
		}

		random = random_m;
		//Initialisation des cellules
		cellules = new int[TAILLE_HAUTEUR][TAILLE_LARGEUR];
		
		if(random) {
			//Cellules de la premi�re ligne initialisé en maniere aléatoire
			Boolean one = false;
			for(int l = 0; l < TAILLE_LARGEUR; l++){
				int nbr = (int)(Math.random()*2);
				cellules[0][l] = nbr;
				if(nbr == 1)
					one = true;
			}
			if(!one)
				cellules[0][(int)(Math.random()*TAILLE_LARGEUR)] = 1;
		} else {
			//Cellule du milieu de la premi�re ligne initialis� � 0
			cellules[0][TAILLE_LARGEUR/2] = 1;
		}
		
		
		//Initialisation ligne courante (on commence � la deuxi�me ligne)
		ligne_courante = 1;
		
		//Cr�ation affichage de l'automate
		creer_affichage(nom);
	}

	private int valProba(int id){
		return (ThreadLocalRandom.current().nextDouble(0, 1) <= proba[id]) ? 1 : 0;
	}
	
	
	/**
	 * M�thode qui permet de trouver la valeur d'une cellule en fonction des r�gle d�fini.
	 * Par exemple : si on a une r�gle qui dit que la cellule vaut 1 si les 3 cases au dessus
	 * valent 1 => on r�cup�re les valeurs des 3 cases au dessus, on regarde si elle respecte
	 * la r�gle et si oui on applique la r�gle.
	 * @param ligne de la cellule que l'on souhaite traiter
	 * @param colonne de la cellule que l'on souhaite traiter
	 * @return 0 ou 1 en fonction des r�gles
	 */
	private int calcul_cellule(int ligne, int colonne) {
		//Valeur de la cellule (de base 0)
		int val = 0;
		
		//R�gle pour les 3 cellules au dessus (voisinage sup�rieur)
		int x = cellules[ligne - 1][colonne - 1];
		int y = cellules[ligne - 1][colonne];
		int z = cellules[ligne - 1][colonne + 1];
		
		//Voisinage sup�rieur (repr�sentation du voisinage par une cha�ne)
		String voisinage = String.valueOf(x) + String.valueOf(y) + String.valueOf(z);
		
		//R�gles
		switch(voisinage) {
		case "000":
			val = (!probaB) ? regles[7] : valProba(7);
			break;
		case "001":
			val = (!probaB) ? regles[6] : valProba(6);
			break;
		case "010":
			val = (!probaB) ? regles[5] : valProba(5);
			break;
		case "011":
			val = (!probaB) ? regles[4] : valProba(4);
			break;
		case "100":
			val = (!probaB) ? regles[3] : valProba(3);
			break;
		case "101":
			val = (!probaB) ? regles[2] : valProba(2);
			break;
		case "110":
			val = (!probaB) ? regles[1] : valProba(1);
			break;
		case "111":
			val = (!probaB) ? regles[0] : valProba(0);
			break;
		}
		
		//Retourne la valeur de la cellule
		return val;
	}
	
	
	/**
	 * M�thode qui permet de r�aliser l'automate en entier
	 * R�aliser l'automate revient � ex�cuter une �tape un nombre de fois
	 * �quivalent au nombre de ligne
	 */
	private void realisation_automate() {
		//On traite chaque ligne
		for (int y=ligne_courante; y < TAILLE_HAUTEUR; y++) {
			evoluer_automate();
		}
	}
	
	
	/**
	 * M�thode qui permet de faire �voluer l'automate d'une �tape
	 * On traite ici seulement une ligne
	 */
	private void evoluer_automate() {		
		//On traite chaque cellule de la ligne (on commence � la deuxi�me colonne et on fini � l'avant derni�re pour �viter les probl�mes)
		for (int x=1; x < TAILLE_LARGEUR - 1; x++) {
			//Calcul de la valeur de la cellule
			cellules[ligne_courante][x] = calcul_cellule(ligne_courante, x);
		}
		

		//On augmente la ligne courante de 1
		ligne_courante++;
	}
	
	
	/*****
	AFFICHAGE
	*****/
	
	
	/**
	 * M�thode qui cr�e l'affichage de l'automate
	 */
	private void creer_affichage(String nom) {
		//Initialisation affichage de l'automate
		fenetre = new JFrame("Automate "+nom);
		//Param�tres fen�tre
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetre.setVisible(true);
		fenetre.setResizable(false);
		
		//Layout
        GridBagLayout layout = new GridBagLayout();
        fenetre.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        //JPanel de l'automate
        jpanel_automate = new JPanel_Automate();
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        fenetre.add(jpanel_automate, gbc);
        
        //Bouton d'�volution
        evolution_automate = new JButton("Evolution automate");
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        fenetre.add(evolution_automate, gbc);
        //Action du bouton
        evolution_automate.addActionListener(new ActionListener() {

        	//Faire �voluer le syst�me
			public void actionPerformed(ActionEvent e) {
				evoluer_automate();
				//Si on d�passe le nombre de ligne de l'automate
				if (!(ligne_courante < TAILLE_HAUTEUR)) {
					evolution_automate.setEnabled(false);
					realisation_automate.setEnabled(false);
				}
				jpanel_automate.repaint();
			}
			
        });
        
        //Bouton de r�alisation
        realisation_automate = new JButton("Finir automate");
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 1;
        fenetre.add(realisation_automate, gbc);
        //Action du bouton
        realisation_automate.addActionListener(new ActionListener() {

        	//Faire �voluer le syst�me
			public void actionPerformed(ActionEvent e) {
				evolution_automate.setEnabled(false);
				realisation_automate.setEnabled(false);
				realisation_automate();
				jpanel_automate.repaint();
			}
			
        });
        
        //Affichage de la fen�tre
        fenetre.pack();
	}
	
	
	/**
	 * JPanel repr�sentant l'automate
	 * @author Th�o
	 *
	 */
	class JPanel_Automate extends JPanel {
		
		private int largeur_cellule = 6;
		
		public JPanel_Automate() {
			this.setPreferredSize(new Dimension(largeur_cellule * TAILLE_LARGEUR, largeur_cellule * TAILLE_HAUTEUR));
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			//Valeur de la cellule
			int cellule;
			
			//Parcours vertical (lignes)
			for (int y=0; y < TAILLE_HAUTEUR; y++) {
				//Parcours horizontal (colonnes)
				for (int x=0; x < TAILLE_LARGEUR; x++) {
					cellule = cellules[y][x];
					
					//Si la cellule vaut 1
					if (cellule == 1) {
						g.setColor(Color.BLACK);
					} else {
						g.setColor(Color.WHITE);
					}
				
					g.fillRect(x * largeur_cellule, y * largeur_cellule, largeur_cellule, largeur_cellule);
				}
			}			
		}
	}
	
	
	/////////////////////////////////////////////////
	/////////////////////////////////////////////////
	
	//Main qui lance le code
	public static void main(String[] args) {

		double[] arrNull = {};
		int[] arr0 = {0,1,1,1,1,1,1,0};
		int[] arr0i = {1,0,0,0,0,0,0,1};
		double[] arrP = {0,0.5,1,1,1,1,0.5,0};
		int[] arrC = {0,0,0,1,1,1,1,0};
		int[] arrS = {0,1,1,0,1,1,1,0};
		int[] arrSm = {0,1,1,1,0,1,1,0};
		
		Automate automate0 = new Automate(arr0, false, arrNull,"0 - vue en cours");
		Automate automate0i = new Automate(arr0i, false, arrNull,"1 - inverse de 0 (vue en cours)");
		Automate automate0p = new Automate(arr0,false, arrP, "2 - vue en cours + proba");
		Automate automateR = new Automate(arr0,true, arrNull, "3 - random initialisation (regles d'automate 0 vue en cpurs)");

		//Automate automate0i = new Automate(arr0i, false, false,"0i - simples");
		Automate automateC = new Automate(arrC, false, arrNull,"4 - ressemble au motif de la coquillage");
		Automate automateCr = new Automate(arrC, true, arrNull,"5 - ressemble au motif de la coquillage + random");

		Automate automateS = new Automate(arrS, false, arrNull,"6 - simples");
		Automate automateSm = new Automate(arrSm,false, arrNull, "7 - simples (mirroire de 6)");

		

		
	}

}

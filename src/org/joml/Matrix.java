/**
 * 
 */
package org.joml;

/**
 * Einf�hrung in die Computergrafik.
 * 
 * Matrixoperationen
 * @author F.N. Rudolph
 * (c) 2012
 *
 */
public class Matrix {

	/**
	 * Drucke Matrix
	 * 
	 * @param c Name der Matrix
	 * @param m Vektor
	 */
	public static void print(String c, double[][] m) {
		for (int i = 0; i < m.length; i++) {
			System.out.print(c + "[ " + i + "][*]: ");
			for (int j = 0; j < m[i].length; j++) {
				System.out.print(m[i][j] + ", ");
			}
			System.out.println(" ");
		}
	}

	/**
	 * Drucke Vektor
	 * 
	 * @param c Name des Vektors
	 * @param m Vektor
	 */
	public static void print(String c, double[] m) {
		for (int i = 0; i < m.length; i++) {
			System.out.println(c + "[ " + i + "]: " + m[i] + ", ");
		}
	}

	/**
	 * Legt die Kopie einer Matrix an
	 * 
	 * @param matrix 
	 * @return clone neue Matrix
	 */
	public static double[][] cloneMatrix(double[][] matrix) {
		int m = matrix.length;
		int n = matrix[0].length;
		double[][] clone = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				clone[i][j] = matrix[i][j];
			}
		}
		return clone;
	}
	
	/**
	 * Berechnet das Produkt eines Vektors mit einem Skalar.
	 * Vektor wird nicht ver�ndert
	 * @param s Faktor
	 * @param vector Vektor
	 * @return s*vector
	 */
	public static double [] produkt(double s, double [] vector){
		int n = vector.length;
		double [] newVec = new double [n];
		for (int i = 0; i < n; ++i){
			newVec [i] = s * vector[i];
		}
		return newVec;
	}

	/**
	 * Berechnet das Produkt einer Matrix mit einem Skalar.
	 * Matrix wird nicht ver�ndert
	 * @param s Faktor
	 * @param matrix Matrix
	 * @return newMatrix s*Matrix
	 */
	public static double [] [] produkt(double s, double [] [] matrix){
		int n = matrix.length;
		int m = matrix[0].length;
		double [] [] newMatrix = new double [n][m];
		for (int i = 0; i < n; ++i){
			for (int j = 0; j < m; ++j){
			newMatrix [i][j] = s * matrix[i][j];
			}
		}
		return newMatrix;
	}

	/**
	 * Matrixmultiplikation matrix3 = matrix1 * matrix2
	 * 
	 * Matrix2 muss so viele Zeilen haben wie Matrix1 Spalten hat.
	 * 
	 * @param matrix1 linke Matrix
	 * @param matrix2 rechte Matrix
	 * @return Matrix3 Ergebnismatrix
	 */
	public static double[][] matMult(double[][] matrix1, double[][] matrix2) {
		// Dimensionen und Schleifengrenzen
		int l = matrix1.length; // Anzahl Zeilen Matrix1
		int m = matrix2.length; // Anzahl Zeilen Matris2
		int n = matrix2[0].length; // Anzahl Spalten Matrix2
		// System.out.println(l + " " + m + " "+ n);

		if (m != matrix1[0].length)
			// throw new RuntimeException();
			throw new IndexOutOfBoundsException("Array bounds incompatible: matrix1 [" + l + "][" + matrix1[0].length
				+ "] * matrix2[" + m + "][" + n + "]");
		else {
			double[][] matrix3 = new double[l][n];
			for (int i = 0; i < n; i++) { // f�r alle Spalten von matrix2 und matrix3
				for (int j = 0; j < l; j++) { // f�r alle Zeilen von matrix1 und matrix3
					matrix3[j][i] = (double) 0.0;
					for (int k = 0; k < m; k++) { // summiere f�r alle Spalten von matrix1 (Zeilen von Matrix2)
						matrix3[j][i] += +matrix1[j][k] * matrix2[k][i];
					}
				}
			}
			return matrix3;
		}
	}// MatMult

	/**
	 * Matrixmultiplikation vektor2 = matrix1 * vektor
	 * 
	 * Vektor muss so viele Zeilen haben wie Matrix1 Spalten hat.
	 * 
	 * @param matrix1 linkes Element
	 * @param vektor rechtes Element
	 * @return vektor2 Ergebnisvektor
	 */
	public static double[] matMult(double[][] matrix1, double[] vektor) {
		// Dimensionen und Schleifengrenzen
		int l = matrix1.length; // Anzahl Zeilen Matrix1
		int m = vektor.length; // Anzahl Zeilen Matris2
		// System.out.println(l + " " + m + " "+ n);

		if (m != matrix1[0].length)
			// throw new RuntimeException();
			throw new IndexOutOfBoundsException("Array bounds incompatible: matrix1 [" + l + "][" + matrix1[0].length
				+ "] * matrix2[" + m + "]");
		else {
			double[] matrix3 = new double[l];
				for (int j = 0; j < l; j++) { // f�r alle Zeilen von matrix1 und matrix3
					matrix3[j] = (double) 0.0;
					for (int k = 0; k < m; k++) { // summiere f�r alle Spalten von matrix1 (Zeilen von Matrix2)
						matrix3[j] += +matrix1[j][k] * vektor[k];
					}
				}
			return matrix3;
		}
	}// matMult

	/**
	 * Invertiere die quadratische Matrix m
	 * 
	 * @param m zu invertierende Matrix
	 * @return r inverse Matrix
	 */
	public static double[][] invertiereMatrix(double[][] m) {
		int n = m.length;
		double[][] tmp = new double[n][2 * n];
		// print("Parameter", m);
		// --- Kopie der Matrix mit Einheitsmatrix rechts
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				tmp[i][j] = m[i][j];
			}
			tmp[i][i + n] = 1d;
		}
		// --- Dreieckselimination
		double[][] tmp1 = Gauss.dreiecksElimination(tmp);
		// --- Rueckwaertssubstitution
		return Gauss.rueckwaertsSubstitutionMehrereGLS(tmp1);

	}

}

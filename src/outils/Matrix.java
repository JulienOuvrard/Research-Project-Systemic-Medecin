package outils;

public class Matrix {

	private float[][] mat;
	private int rows;
	private int cols;
	
	public Matrix(int r,int c){
		rows=r;
		cols=c;
		mat=new float[rows][cols];
	}
	public void add(float elem,int r, int c){
		mat[r][c]=elem;
	}
	public float[] getRowAtIndex(int r){
		return mat[r];
	}
	public void affiche(){
		System.out.println("--------------");
		for(int i=0;i<rows;++i){
			System.out.print("|");
			for(int j=0;j<cols;++j){
				System.out.print(mat[i][j]+"\t |");
			}
			System.out.println("");
		}
		System.out.println("--------------");
	}
}

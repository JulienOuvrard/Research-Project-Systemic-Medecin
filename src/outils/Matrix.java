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
	public float[][] getMat(){
		return mat;
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
	public String[][] toStringArray(){
		String[][] res= new String[rows][cols];
		for(int r=0;r<rows;r++){
			for(int c=0;c<cols;c++){
				res[r][c]=Float.toString(mat[r][c]);
			}
		}
		return res;
	}
	public String[][] toStringArrayWithElements(String[] elem){
		String[][] res= new String[rows][cols+1];
		for(int r=0;r<rows;r++){
			res[r][0]=elem[r];
			for(int c=1;c<cols+1;c++){
				res[r][c]=Float.toString(mat[r][c-1]);
			}
		}
		return res;
	}
}

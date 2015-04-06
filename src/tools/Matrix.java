package tools;

/**
 * A tool class implementing matrix
 * @author Julien OUVRARD
 *
 */
public class Matrix {

	private float[][] mat;
	private int rows;
	private int cols;
	
	/**
	 * Constructor of the class Matrix
	 * @param r Number of rows
	 * @param c Number of columns
	 */
	public Matrix(int r,int c){
		rows=r;
		cols=c;
		mat=new float[rows][cols];
	}
	
	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public void setMat(float[][] mat) {
		this.mat = mat;
	}
	
	public float[][] getMat(){
		return mat;
	}
	
	/**
	 * Add an element in the matrix
	 * @param elem The element to add
	 * @param r The row of the element
	 * @param c The column of the element
	 */
	public void add(float elem,int r, int c){
		mat[r][c]=elem;
	}
	
	/**
	 * A printing function
	 */
	public void print(){
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
	
	/**
	 * 
	 * @return The matrix with strings
	 */
	public String[][] toStringArray(){
		String[][] res= new String[rows][cols];
		for(int r=0;r<rows;r++){
			for(int c=0;c<cols;c++){
				res[r][c]=Float.toString(mat[r][c]);
			}
		}
		return res;
	}
	
	/**
	 * 
	 * @param elem Elements to add to the string matrix
	 * @return The matrix with the elements
	 */
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

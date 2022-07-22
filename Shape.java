package shapes;

public class Shape {
	private int x, y;
        private String color;
	private int width;
	public Shape(int x, int y) {
		// super();
		this.x = x;
		this.y = y;
	}
	

	@Override
	public String toString() {
		System.out.println("in shape's tostring");
	//	System.out.println(super.toString());
		return x + " " + y;
	}
	//area
	public double area()
	{ // calculate area of shape.
		System.out.println("un defined area");
		return -1;
	}

}

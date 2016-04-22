package LWJGL;

/**
 * Created by Rene on 20.04.2016.
 */

public class Main
{
	public static void main(String[] args)
	{
		System.out.println("Übungsaufgabe 1");
		Controller master = new Controller();
		DreieckUebung dreieck = new DreieckUebung();
		master.addSubController(new SubController(0,dreieck).getFrame());
		master.addSubController(new SubController(1,dreieck).getFrame());
		master.addSubController(new SubController(2,dreieck).getFrame());
		master.SetVisible();
		dreieck.run();
	}
}

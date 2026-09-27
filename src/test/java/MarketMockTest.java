import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.mockito.Mockito;

import businessLogic.BLFacade;
import gui.MainGUI;

public class MarketMockTest {
	
	static BLFacade appFacadeMock = Mockito.mock(BLFacade.class);
	
	public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		MainGUI sut = new MainGUI();
		MainGUI.setBussinessLogic(appFacadeMock);
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		sut.setVisible(true);
	}
	 
}
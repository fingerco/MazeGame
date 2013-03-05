import java.awt.image.BufferedImage;

abstract public class GridBlocks implements EventListener{

	abstract public void addBlock(Block block);
	abstract public BufferedImage getImage();
}

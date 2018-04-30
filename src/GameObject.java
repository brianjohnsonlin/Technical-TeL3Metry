public class GameObject {
    protected Image Sprite;

    public GameObject(String ImageFilename, int width, int height, int x, int y) {
        Sprite = new Image(ImageFilename, width, height, x, y);
    }

    public GameObject(String ImageFilename, int x, int y) {
        Sprite = new Image(ImageFilename, x, y);
    }

    public Image GetSprite() {
        return Sprite;
    }
}

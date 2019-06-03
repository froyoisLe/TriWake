package site.isleti.triwake.event;


public class StartUpdateIntrosEvent {

    private int position;

    public StartUpdateIntrosEvent(int position) {
        this.position = position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
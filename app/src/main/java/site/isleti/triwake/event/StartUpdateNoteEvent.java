package site.isleti.triwake.event;


public class StartUpdateNoteEvent {

    private int position;

    public StartUpdateNoteEvent(int position) {
        this.position = position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
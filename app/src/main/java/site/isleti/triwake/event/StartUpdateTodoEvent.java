package site.isleti.triwake.event;


public class StartUpdateTodoEvent {

    private int position;

    public StartUpdateTodoEvent(int position) {
        this.position = position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
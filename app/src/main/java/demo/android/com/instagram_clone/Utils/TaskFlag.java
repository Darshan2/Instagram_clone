package demo.android.com.instagram_clone.Utils;

/**
 * Created by Darshan B.S on 07-06-2018.
 */

class TaskFlag {
    private static final TaskFlag ourInstance = new TaskFlag();
    private boolean flag;

    static TaskFlag getInstance() {
        return ourInstance;
    }

    private TaskFlag() {
    }

    public boolean isTaskSuccess() {
        return flag;
    }

    public void setTaskSuccess(boolean flag) {
        this.flag = flag;
    }
}

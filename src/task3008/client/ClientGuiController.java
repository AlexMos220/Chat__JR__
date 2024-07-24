package task3008.client;

public class ClientGuiController extends Client{

    private final ClientGuiModel model = new ClientGuiModel();
    private final ClientGuiView view = new ClientGuiView(this);


    public ClientGuiModel getModel(){
        return model;
    }
    public class GuiSocketThread extends SocketThread{

        public void processIncomingMessage(String message){

            model.setNewMessage(message);
            view.refreshMessages();
        }
        public void informAboutAddingNewUser(String userName){

            model.addUser(userName);
            view.refreshUsers();
        }
        public void informAboutDeletingNewUser(String userName){
            model.deleteUser(userName);
            view.refreshUsers();
        }
        public void notifyConnectionStatusChanged(boolean clientConnected){
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }

    protected SocketThread getSocketThread(){
        return new GuiSocketThread();
    }

   public void run(){
        getSocketThread().run();
   }

    @Override
    protected String getServerAddress() {
        return view.getServerAddress();
    }

    @Override
    protected int getServerPort() {
        return view.getServerPort();
    }

    @Override
    protected String getUserName() {
        return view.getUserName();
    }

    public static void main(String[] args) {
        Client gui = new ClientGuiController();
        gui.run();
    }
}

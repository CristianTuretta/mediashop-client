package view;

import Controller.ClickListener;
import exceptions.BuyingException;
import exceptions.SqlConnectionException;
import exceptions.RegistrationException;
import exceptions.UserNotRegisteredException;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Cart;
import model.Product;
import model.Track;
import model.User;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Cristian Turetta on 10/07/17.
 */
public class View extends Application{
    /**
     * Data
     * */
    private User loggedUser = null;
    private ArrayList<Product> allProducts = null;
    private Cart cart = new Cart();
    ClickListener listener;

    /**
     * View Components
     * */
    public VBox sceneConainer = new VBox(8);
    public GridPane topGridPane = new GridPane();
    public HBox logoContiner = new HBox(8);
    public HBox searchItemsContainer = new HBox(8);
    public HBox userInfoContainer = new HBox(8);
    public HBox cartContainer = new HBox(8);
    public ScrollPane scrollContainer = new ScrollPane();
    public TilePane tile;

    public ChoiceBox searchFilterChoiceBox = new ChoiceBox();
    public TextField searchBarTextField = new TextField();
    public Button searchButton;

    public Hyperlink loginLink = new Hyperlink("LogIn");
    public Hyperlink signupLink = new Hyperlink("SignUp");
    public Hyperlink logoutLink = new Hyperlink("LogOut");
    public Hyperlink usernameLink = new Hyperlink();
    public Hyperlink myOrdrdesLink = new Hyperlink("My orders");
    public Node[] UserItems = {loginLink, signupLink, usernameLink, myOrdrdesLink, logoutLink};

    public Button cartButton;

    // Login pop-up
    Stage logInStage;
    public Button loginButton;
    public PasswordField loginPopUpPswField = new PasswordField();
    public TextField loginPopUpUsernameTextField = new TextField();
    public Label loginFeedbackLabel = new Label("Wrong username or password");

    // Signup pop-up
    Stage signupStage;
    public Label usernameLabel;
    public TextField usernameTextField;
    Label passwordLabel;
    public PasswordField passwordField;
    Label checkPasswordLabel;
    public PasswordField checkPasswordField;
    public Label passwordCheckResultLabel;
    Label fiscalCodeLabel;
    public TextField fiscalCodeTextField;
    Label nameLabel;
    public TextField nameTextField;
    Label surnameLabel;
    public TextField surnameTexField;
    Label cityLabel;
    public TextField cityTextField;
    Label telephoneLabel;
    public TextField telephoneNumberTextField;
    Label optionalDescriptionLabel;
    public TextField mobilePhoneNumberTextField;
    HBox wrapper;
    public Button submitRegistationButton = new Button("Submit");

    /**
     * Initialize the stage whit the given pane dimensions
     * @param stage
     * @param backbone
     * @param width
     * @param height
     * */
    private void initNewWindowWhit(Stage stage, Pane backbone, int width, int height){
        backbone.setMinSize(width, height);
        Scene newScene = new Scene(backbone);
        stage.setScene(newScene);
        stage.show();
    }

    /**
     * Setting up an horizontal content Pane with more elements
     * @param container
     * @param elements
     * */
    private void setupHorizontalLayouts(Pane container, Node[] elements){
        container.setPadding(new Insets(24,0,8,0));
        for (Node element:elements){
            container.getChildren().add(element);
        }
    }

    /**
     * Setting up an horizontal content Pane with a single element
     * @param container
     * @param element
     * */
    private void setupHorizontalLayouts(Pane container, Node element, Insets padding){
        container.setPadding(padding);
        container.getChildren().add(element);
    }

    /**
     * Returns a new ImageView
     * @param imgPath
     * @param width
     * @param height
     * */
    private ImageView initImageView(String imgPath, int width, int height){
        Image newImage = null;
        try{
            newImage = new Image(getClass().getResource(imgPath).toURI().toString());
            ImageView generatedImageView = new ImageView(newImage);
            generatedImageView.preserveRatioProperty();
            generatedImageView.setFitWidth(width);
            generatedImageView.setFitHeight(height);
            generatedImageView.setCache(true);
            generatedImageView.setSmooth(true);
            return generatedImageView;
        }catch (Exception e){
            return new ImageView();
        }
    }

    /**
     * Inits all values of Search items
     * */
    private void initSearchItems() throws Exception {
        searchFilterChoiceBox.setItems(FXCollections.observableArrayList("all", new Separator(),"by genre","by soloist","by bandname"));
        searchFilterChoiceBox.getSelectionModel().selectFirst();
        searchBarTextField.setMinWidth(400);
        searchBarTextField.setPromptText("Search...");
        searchButton = new Button("", initImageView("assets/search.png",17,17));
        searchButton.setOnAction(listener);
    }

    /**
     * Init view for non-logged users
     * @param elements
     * @param container
     * */
    private void initUserItems(HBox container, Node[] elements){
        container.setAlignment(Pos.CENTER);
        container.setMinWidth(300);
        loginLink.setVisible(true);
        signupLink.setVisible(true);
        usernameLink.setVisible(false);
        myOrdrdesLink.setVisible(false);
        logoutLink.setVisible(false);
        setupHorizontalLayouts(container, elements);
    }

    /**
     * Init view for logged users
     * @param elements
     * @param container
     * @param authUser
     * */
    private void initUserItems(HBox container, Node[] elements, User authUser){
        container.setAlignment(Pos.CENTER);
        container.setMinWidth(300);
        usernameLink.setVisible(true);
        myOrdrdesLink.setVisible(true);
        logoutLink.setVisible(true);
        loginLink.setVisible(false);
        signupLink.setVisible(false);
        usernameLink.setText(authUser.getUsername());
    }

    /**
     * search the string on db
     * @param scroll
     * */
    public void searchIt(ScrollPane scroll) throws Exception {
        switch (searchFilterChoiceBox.getValue().toString()){
            case "all":
                allProducts = Product.searchProductBy(searchBarTextField.getText());
                break;
            case "by bandname":
                allProducts = Product.getProductsByBand(searchBarTextField.getText());
                break;
            case "by soloist":
                allProducts = Product.getProductsBySoloist(searchBarTextField.getText());
                break;
            case "by genre":
                allProducts = Product.getProductsByGenre(searchBarTextField.getText());
                break;
        }
        displayProducts(allProducts, scroll);
    }

    /**
     * Show loig in pop up
     * @param primaryStage
     * */
    public void loging(Stage primaryStage){
        logInStage = new Stage();
        logInStage.setTitle("LOG IN");
        logInStage.initModality(Modality.APPLICATION_MODAL);
        logInStage.initOwner(primaryStage);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        loginPopUpUsernameTextField.setPromptText("username");
        grid.add(loginPopUpUsernameTextField, 1, 1);


        loginPopUpPswField.setPromptText("password");
        grid.add(loginPopUpPswField, 1, 2);

        Image image = null;
        try {   image = new Image(getClass().getResource("assets/icon_key_black.png").toURI().toString()); }
        catch(URISyntaxException e1){ System.out.println(e1);}

        ImageView iview = new ImageView();
        iview.setImage(image);
        iview.setPreserveRatio(true);
        iview.setSmooth(true);
        iview.setCache(true);

        loginButton = new Button("LOGIN",iview);
        loginButton.setOnAction(listener);

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BASELINE_RIGHT);
        hbBtn.getChildren().add(loginButton);
        grid.add(hbBtn, 1, 3);

        loginFeedbackLabel.setVisible(false);

        grid.add(loginFeedbackLabel, 1, 5);

        Scene dialogScene = new Scene(grid, 300, 200);

        logInStage.setScene(dialogScene);
        logInStage.show();
    }

    /**
     * Send login request
     * @param params
     * */
    public void loginRequest(List<NameValuePair> params){
        try {
            loggedUser = User.loginWithUser(params);
            if (loggedUser != null){
                initUserItems(userInfoContainer, UserItems, loggedUser);
                logInStage.close();
            }

        }catch (SqlConnectionException e){
            e.printStackTrace();
        }catch (UserNotRegisteredException e){
            // display user o psw errata
            loginFeedbackLabel.setTextFill(Color.FIREBRICK);
            loginFeedbackLabel.setVisible(true);
        }
    }

    /**
     * Show sign in pop up
     * @param primaryStage
     * */
    public void signingUp(Stage primaryStage){
        signupStage = new Stage();
        signupStage.setTitle("SIGN UP");
        signupStage.initModality(Modality.APPLICATION_MODAL);
        signupStage.initOwner(primaryStage);

        // Grid settings
        VBox v_signup = new VBox(8);
        v_signup.setPadding(new Insets(8,8,8,8));

        usernameLabel = new Label("Username");
        usernameTextField = new TextField();
        usernameTextField.setPromptText("Username");

        passwordLabel = new Label("Password");
        passwordField = new PasswordField();
        passwordField.setPromptText("Type a password");

        checkPasswordLabel = new Label("Rewrite Password");
        checkPasswordField = new PasswordField();
        checkPasswordField.setPromptText("Retype the password");

        passwordCheckResultLabel = new Label();
        passwordCheckResultLabel.setVisible(false);

        fiscalCodeLabel = new Label("Fiscal Code");
        fiscalCodeTextField = new TextField();
        fiscalCodeTextField.setPromptText("Type your fiscal code");

        nameLabel = new Label("Name");
        nameTextField = new TextField();
        nameTextField.setPromptText("Name");

        surnameLabel = new Label("Surname");
        surnameTexField = new TextField();
        surnameTexField.setPromptText("Surname");

        cityLabel = new Label("City");
        cityTextField = new TextField();
        cityTextField.setPromptText("Your City");

        telephoneLabel = new Label("Telephone");
        telephoneNumberTextField = new TextField();
        telephoneNumberTextField.setPromptText("Your telephone number");

        optionalDescriptionLabel = new Label("Optional mobile phone");

        mobilePhoneNumberTextField = new TextField();
        mobilePhoneNumberTextField.setPromptText("Mobile phone number");


        wrapper = new HBox();
        wrapper.getChildren().add(submitRegistationButton);
        submitRegistationButton.setOnAction(listener);
        wrapper.setAlignment(Pos.BASELINE_RIGHT);
        wrapper.setPadding(new Insets(8,0,0,0));

        Node[] fields = {usernameLabel, usernameTextField, passwordLabel,
                passwordField, checkPasswordLabel, checkPasswordField,
                passwordCheckResultLabel, fiscalCodeLabel, fiscalCodeTextField, nameLabel,
                nameTextField, surnameLabel, surnameTexField,
                cityLabel, cityTextField, telephoneLabel,
                telephoneNumberTextField, optionalDescriptionLabel, mobilePhoneNumberTextField, wrapper};

        for (Node node: fields) {
            v_signup.getChildren().add(node);
        }



        v_signup.isFillWidth();
        Scene dialogScene = new Scene(v_signup);

        signupStage.setScene(dialogScene);
        signupStage.setOpacity(0.95);
        signupStage.setMinWidth(500);
        signupStage.show();

    }


    public void sigupRequest(List<NameValuePair> params){
        try {
            User.registerNewUser(params);
        }catch (RegistrationException e){
            e.printStackTrace();
            //TODO: insert a dialog
        }
    }


    /**
     * Log out the current user
     * */
    public void loggingOut() {
        loggedUser = null;
        initUserItems(userInfoContainer, UserItems);
    }

    /**
     * Show a pop up with the cart content
     * @param primaryStage
     * */
    public void showCart(Stage primaryStage){
        Stage cartInStage = new Stage();
        cartInStage.setTitle("CART");
        cartInStage.initModality(Modality.APPLICATION_MODAL);
        cartInStage.initOwner(primaryStage);

        VBox v_cart = new VBox();


        Label l_payment = new Label("PAYMENT METHOD");
        ChoiceBox cb_payment = new ChoiceBox<String>();
        cb_payment.setItems(FXCollections.observableArrayList(
                "Bankwire","Credit Card","Paypal")
        );
        cb_payment.getSelectionModel().selectFirst();

        v_cart.getChildren().addAll(l_payment,cb_payment,new Separator());


        ArrayList<Product> cartProducts = cart.getCartContent();

        for(int i=0; i< cartProducts.size(); i++){
            HBox h_cart = new HBox(5);
            h_cart.setAlignment(Pos.BASELINE_RIGHT);

            Product p = cartProducts.get(i);
            Label l_name = new Label(p.getTitle());
            TextField f_quantity = new TextField(cart.getProductQuantity(p).toString());

            Button b_changeQuantity = new Button("Change");
            f_quantity.setMinWidth(25);

            b_changeQuantity.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    cart.setProductQuantity(p,Integer.parseInt(f_quantity.getText()));

                }
            });


            h_cart.getChildren().addAll(l_name, f_quantity, b_changeQuantity);
            v_cart.getChildren().add(h_cart);
        }

        Button btn_checkout = new Button("CHECKOUT");

        btn_checkout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cart.checkoutCart(loggedUser,cb_payment.getValue().toString());
                VBox v_checkout = new VBox();
                Label l_checkout = new Label("Almeno uno o più prodotti in quantità insufficiente");
                try {
                    if (cart.checkoutCart(loggedUser, cb_payment.getValue().toString())) {
                        l_checkout.setText("Grazie per l'acquisto!");
                    }
                }catch (BuyingException bexc){
                    l_checkout.setText("Errore nel db!");
                    bexc.printStackTrace();
                }
                v_checkout.getChildren().add(l_checkout);

                Scene dialog = new Scene(v_checkout);
                cartInStage.setScene(dialog);
                cartInStage.setOpacity(0.95);
                cartInStage.setMinWidth(50);
                cartInStage.show();
            }
        });

        if(loggedUser == null)
            btn_checkout.setVisible(false);
        else
            btn_checkout.setVisible(true);

        v_cart.getChildren().addAll(new Separator(),btn_checkout);

        Scene dialog = new Scene(v_cart);
        cartInStage.setScene(dialog);
        cartInStage.setOpacity(0.95);
        cartInStage.setMinWidth(500);
        cartInStage.show();

    }

    /**
     * Inits a scrollPane with the given tile
     * @param container
     * @param tile
     * */
    private void initScrollContainer(ScrollPane container, TilePane tile){
        container.setMinSize(600, 650);
        scrollContainer.setContent(tile);
    }

    public void start(Stage primaryStage) throws Exception {
        listener = new ClickListener(this, primaryStage);

        loginLink.setOnAction(listener);
        signupLink.setOnAction(listener);
        logoutLink.setOnAction(listener);

        initNewWindowWhit(primaryStage, sceneConainer, 1200, 800);
        ImageView mediashopLogoImageView = initImageView("assets/mediashop-logo.png", 250,50);
        setupHorizontalLayouts(logoContiner, mediashopLogoImageView, new Insets(8,8,8,8));

        topGridPane.add(logoContiner,0,0);

        initSearchItems();
        Node[] searchItems = {searchFilterChoiceBox, searchBarTextField, searchButton};
        setupHorizontalLayouts(searchItemsContainer, searchItems);

        topGridPane.add(searchItemsContainer,1,0);

        userInfoContainer.setAlignment(Pos.CENTER);
        initUserItems(userInfoContainer, UserItems);

        topGridPane.add(userInfoContainer, 2,0);

        cartButton = new Button("", initImageView("assets/cart.png",17,17));
        cartButton.setOnAction(listener);
        setupHorizontalLayouts(cartContainer, cartButton, new Insets(24,8,8,8));

        topGridPane.add(cartContainer, 3,0);

        initScrollContainer(scrollContainer, tile);
        sceneConainer.getChildren().add(topGridPane);
        sceneConainer.getChildren().add(scrollContainer);

        displayProducts(Product.getAllProducts(), scrollContainer);
    }

    public void displayProducts(ArrayList<Product> products, ScrollPane scroll){
        int i;
        tile = new TilePane();
        tile.setPrefColumns(4);
        for(i=0; i< products.size(); i++){
            VBox container = new VBox(8);
            container.setMinSize(290,400);
            container.setMaxSize(290,400);
            container.setPadding(new Insets(8));

            String coverUrl = products.get(i).getUrl_cover();
            ImageView coverImageView = initImageView("assets/cdDefaultCoverImg.png",280,280);
            if(coverUrl != null)
                coverImageView.setImage(new Image(coverUrl));

            StackPane coverImageContainer = new StackPane();
            setProductDetailsContainer(coverImageContainer, coverImageView, products.get(i));

            container.getChildren().add(coverImageContainer);
            tile.getChildren().add(container);

            VBox infoContainer = new VBox(8);
            setProductInfoContainer(infoContainer, products.get(i));

            container.getChildren().add(infoContainer);
        }
        scroll.setContent(tile);
    }

    /**
     * Setup product's footer infos
     * @param footer
     * @param product
     * */
    private void setProductInfoFooterContainer(HBox footer, Product product){
        footer.setAlignment(Pos.CENTER);
        Label availableLabel = new Label("n° " + product.getQuantity());

        Button addToCartButton = new Button(product.getPrice() +  " €", initImageView("assets/plus.png", 16,16));

        ImageView productTypeImageView = new ImageView();
        switch (product.getType()){
            case "CD":
                productTypeImageView = initImageView("assets/cd-filled.png",32,24);
                break;
            case "DVD":
                productTypeImageView = initImageView("assets/dvd-filled.png", 32,24);
                break;
        }

        Node[] footerItems = {availableLabel, addToCartButton, productTypeImageView};
        for (Node item:footerItems) {
            footer.getChildren().add(item);
        }

        addToCartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cart.addItem(product, 1);
                //TODO: create a counter
            }
        });
    }

    /**
     * Set artist label
     * @param product
     * @param artistLabel
     * */
    private void setProductInfoArtistLabel(Product product, Label artistLabel){
        if (product.getSoloist().getStageName() == null){
            artistLabel.setText(product.getBandName());
        }else{
            artistLabel.setText(product.getSoloist().getStageName());
        }
    }

    /**
     * Set product infos
     * @param container
     * @param product
     * */
    private void setProductInfoContainer(VBox container, Product product){
        container.setAlignment(Pos.CENTER);
        Label titleLabel = new Label(product.getTitle());

        Label artistLabel = new Label();
        setProductInfoArtistLabel(product, artistLabel);

        HBox footerContainer = new HBox();
        setProductInfoFooterContainer(footerContainer, product);

        Node[] items = {titleLabel, artistLabel, footerContainer};

        for (Node item: items) {
            container.getChildren().add(item);
        }
    }

    /**
     * Set product details
     * @param container
     * @param backgoundImageView
     * @param product
     * */
    private void setProductDetailsContainer(StackPane container, ImageView backgoundImageView, Product product){
        VBox productDetailsContainer = new VBox();
        productDetailsContainer.setPadding(new Insets(24,8,8,8));
        productDetailsContainer.setStyle("-fx-background-color: white;");

        Label descriptionLabel = new Label("Description:");
        Label productDescriptionLabel = new Label(product.getDescription());
        productDescriptionLabel.setWrapText(true);
        productDescriptionLabel.setTextAlignment(TextAlignment.JUSTIFY);
        productDescriptionLabel.setMaxSize(280,280);

        Label trackDescriptionLabel = new Label("\nTracks:\n");
        trackDescriptionLabel.setWrapText(true);

        Node[] viewItems = {descriptionLabel, productDescriptionLabel, trackDescriptionLabel};
        for (Node item:viewItems){
            productDetailsContainer.getChildren().add(item);
        }

        for (Track track: product.getTracks()){
            Label trackLabel = new Label(track.getTrackWithPosition());
            trackLabel.setMaxWidth(280);
            trackLabel.setPadding(new Insets(0,0,0,16));
            productDetailsContainer.getChildren().add(trackLabel);
        }

        productDetailsContainer.setOpacity(0.8);
        productDetailsContainer.setVisible(false);
        container.getChildren().add(backgoundImageView);
        container.getChildren().add(productDetailsContainer);

        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (productDetailsContainer.isVisible()){
                    productDetailsContainer.setVisible(false);
                }else {
                    productDetailsContainer.setVisible(true);
                }
            }
        });


    }

    public static void main(String[] args) {
        launch();
    }
}
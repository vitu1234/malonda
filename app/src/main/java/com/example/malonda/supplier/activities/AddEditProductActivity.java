package com.example.malonda.supplier.activities;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malonda.R;
import com.example.malonda.adapters.CategoryAutoCompleteAdapter;
import com.example.malonda.adapters.UnitsAutoCompleteAdapter;
import com.example.malonda.api.RetrofitClient;
import com.example.malonda.common.SplashscreenActivity;
import com.example.malonda.models.AllDataResponse;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.Product;
import com.example.malonda.models.Unit;
import com.example.malonda.models.User;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.utils.BetterActivityResult;
import com.example.malonda.utils.CheckInternet;
import com.example.malonda.utils.MyProgressDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.mrntlu.toastie.Toastie;
import com.squareup.picasso.Picasso;
import com.sandrios.sandriosCamera.internal.SandriosCamera;
import com.sandrios.sandriosCamera.internal.configuration.CameraConfiguration;
import com.sandrios.sandriosCamera.internal.ui.model.Media;

import androidx.activity.result.ActivityResult;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

public class AddEditProductActivity extends AppCompatActivity {
    AppDatabase room_db;
    ArrayList<Category> arrayListCategories = new ArrayList<>();
    ArrayList<Unit> unitArrayList = new ArrayList<>();

    Call<AllDataResponse> call;
    private List<User> userList;
    private List<Category> categoryList;
    private List<BusinessInfo> businessInfoList;
    private List<Product> productList;
    private List<Unit> unitList;

    AutoCompleteTextView acTextViewCategory, acTextViewUnit;

    TextInputLayout textInputLayoutCategory, textInputLayoutUnit, textInputLayoutQty, textInputLayoutProName, textInputLayoutPrice, textInputLayoutThreshold, textInputLayoutDesc;

    CircleImageView circleImageViewItemPic;
    TextView textViewTitle;

    ImageView imageViewChangeProdPic, imageViewDeleteProduct;

    MyProgressDialog progressDialog;
    int category_id = -1, user_id = -1, product_id = -1,unit_id = -1;
    CheckInternet checkInternet;

    //properties
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);
    File imgFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        room_db = AppDatabase.getDbInstance(this);
        progressDialog = new MyProgressDialog(this);
        checkInternet = new CheckInternet(this);

        acTextViewCategory = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        acTextViewUnit = (AutoCompleteTextView) findViewById(R.id.autoCompleteSupplierTextView);
        circleImageViewItemPic = findViewById(R.id.productImageAdd);
        textInputLayoutProName = findViewById(R.id.productNameAdd);
        textInputLayoutPrice = findViewById(R.id.productItemPriceAdd);
        textInputLayoutThreshold = findViewById(R.id.productItemThresholdAdd);
        textInputLayoutDesc = findViewById(R.id.productNotesAdd);
        textInputLayoutCategory = findViewById(R.id.productCategoryAdd);
        textInputLayoutUnit = findViewById(R.id.productSupplierAdd);
        textInputLayoutQty = findViewById(R.id.productItemQtyAdd);
        textViewTitle = findViewById(R.id.product_title);
        imageViewChangeProdPic = findViewById(R.id.pickProductPicAdd);
        imageViewDeleteProduct = findViewById(R.id.cancelProductPicAdd);

        categoriesDropDown();
        unitsDropDown();

        Intent intent = getIntent();

        // There are no request codes
        if (intent.getIntExtra("product_id", -1) > 0) {
            product_id = intent.getIntExtra("product_id", -1);
            setEditProductViews();
            textViewTitle.setText("View|Edit product");
        } else {
            imageViewChangeProdPic.setVisibility(View.GONE);
            imageViewDeleteProduct.setVisibility(View.GONE);
        }

        user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();
    }
    private void setEditProductViews() {
        Product product = room_db.productDao().findByProductId(product_id);
        String imageUri = RetrofitClient.BASE_URL2 + "images/products/" + product.getImg_url();

        Picasso.get().load(imageUri)
                .placeholder(R.drawable.my_products_icon)
                .error(R.drawable.my_products_icon)
                .into(circleImageViewItemPic);

        textInputLayoutProName.getEditText().setText(product.getProduct_name());
        textInputLayoutPrice.getEditText().setText(product.getPrice() + "");
        textInputLayoutThreshold.getEditText().setText(product.getThreshold()+"");
        textInputLayoutQty.getEditText().setText(product.getQty() + "");
        textInputLayoutDesc.getEditText().setText(product.getDescription());
        category_id = product.getCategory_id();
        user_id = product.getUser_id();
        unit_id = product.getUnit_id();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            acTextViewUnit.setText(room_db.unitDao().findByUnitId(unit_id).getUnit_name() + room_db.unitDao().findByUnitId(unit_id).getUnit_symbol());
            acTextViewCategory.setText(room_db.categoryDao().findByCategoryId(category_id).getCategory_name());

            acTextViewCategory.dismissDropDown();
            acTextViewUnit.dismissDropDown();
            acTextViewCategory.clearFocus();
            acTextViewUnit.clearFocus();
        }, 100);


    }

    private void categoriesDropDown() {
        for (int i = 0; i < room_db.categoryDao().getAllCategorys().size(); i++) {
            arrayListCategories.add(room_db.categoryDao().getAllCategorys().get(i));
        }


        //Set the number of characters the user must type before the drop down list is shown
        //Set the adapter
        CategoryAutoCompleteAdapter categoryAutoCompleteAdapter = new CategoryAutoCompleteAdapter(this, arrayListCategories);
        acTextViewCategory.setThreshold(0);
        acTextViewCategory.setAdapter(categoryAutoCompleteAdapter);

        acTextViewCategory.setOnItemClickListener((parent, view, position, id) -> {
            Category model = (Category) categoryAutoCompleteAdapter.getItem(position);
            acTextViewCategory.setText(model.getCategory_name() + " ");
            acTextViewCategory.setSelection(model.getCategory_name().length());
            category_id = model.getCategory_id();
        });

        acTextViewCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (product_id == -1) {
                    acTextViewCategory.setText(" ");
                    acTextViewCategory.showDropDown();
                } else {
                    acTextViewCategory.setText(room_db.categoryDao().findByCategoryId(category_id).getCategory_name());
                }

            }
        });

        acTextViewCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                acTextViewCategory.showDropDown();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                acTextViewCategory.showDropDown();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void unitsDropDown() {
        for (int i = 0; i < room_db.unitDao().getAllUnits().size(); i++) {
            unitArrayList.add(room_db.unitDao().getAllUnits().get(i));
        }


        //Set the number of characters the user must type before the drop down list is shown
        //Set the adapter
        UnitsAutoCompleteAdapter unitsAutoCompleteAdapter = new UnitsAutoCompleteAdapter(this, unitArrayList);
        acTextViewUnit.setThreshold(0);
        acTextViewUnit.setAdapter(unitsAutoCompleteAdapter);
        unitsAutoCompleteAdapter.notifyDataSetInvalidated();

        acTextViewUnit.setOnItemClickListener((parent, view, position, id) -> {
            Unit model = (Unit) unitsAutoCompleteAdapter.getItem(position);
            acTextViewUnit.setText(model.getUnit_name()+" - "+model.getUnit_symbol());
            acTextViewUnit.setSelection(model.getUnit_name().length());
            unit_id = model.getUnit_id();
//            unit_id = room_db.unitDao().findByUnitId(unit_id).getUnit_id();
        });

        acTextViewUnit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {


                if (unit_id == -1) {
                    acTextViewUnit.setText(" ");
                    acTextViewUnit.showDropDown();
                } else {
                    acTextViewUnit.setText(room_db.unitDao().findByUnitId(unit_id).getUnit_name());
                }


            }
        });

        acTextViewUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                acTextViewUnit.showDropDown();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                acTextViewUnit.showDropDown();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void goback(View view) {
        onBackPressed();
    }

    public void pickImage(View view) {
        launchCamera();
    }

    private void launchCamera() {
        SandriosCamera
                .with()
                .setShowPicker(true)
                .setVideoFileSize(20)
                .setMediaAction(CameraConfiguration.MEDIA_ACTION_BOTH)
                .enableImageCropping(true)
                .launchCamera(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
                && requestCode == SandriosCamera.RESULT_CODE
                && data != null) {
            if (data.getSerializableExtra(SandriosCamera.MEDIA) instanceof Media) {
                Media media = (Media) data.getSerializableExtra(SandriosCamera.MEDIA);

                Log.e("File", "" + media.getPath());
                Log.e("Type", "" + media.getType());


                if (media.getType() == 1) {
                    progressDialog.showErrorToast("Video not allowed!");
                } else {
                    imgFile = new File(media.getPath());
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    circleImageViewItemPic.setImageBitmap(myBitmap);
//                    Toast.makeText(getApplicationContext(), "Media captured.", Toast.LENGTH_SHORT).show();
                    if (product_id != -1) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (checkInternet.isInternetConnected(this)) {
                                changeProductPicture();
                            } else {
                                checkInternet.showInternetDialog(this);
                            }
                        }, 100);
                    }


                }

            }
        }
    }

    private void changeProductPicture() {
        progressDialog.showDialog("Please wait...");
        if (imgFile != null && product_id != -1) {
            RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), imgFile);
            MultipartBody.Part partImage = MultipartBody.Part.createFormData("file", imgFile.getName(), reqBody);
            call = RetrofitClient.getInstance().getApi().changeProductImage(partImage, product_id);
            call.enqueue(new Callback<AllDataResponse>() {
                @Override
                public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                    AllDataResponse response1 = response.body();
                    progressDialog.closeDialog();
                    if (response1 != null) {
                        if (!response1.isError()) {

                            room_db.clearAllTables();

                            userList = response1.getUsers();
                            for (int i = 0; i < userList.size(); i++) {
                                room_db.userDao().insertUser(userList.get(i));
                            }

                            businessInfoList = response1.getBusiness_info();
                            for (int i = 0; i < businessInfoList.size(); i++) {
                                room_db.businessInfoDao().insertBusinessInfo(businessInfoList.get(i));
                            }

                            categoryList = response1.getCategories();
                            for (int i = 0; i < categoryList.size(); i++) {
                                room_db.categoryDao().insertCategory(categoryList.get(i));
                            }

                            productList = response1.getProducts();
                            for (int i = 0; i < productList.size(); i++) {
                                room_db.productDao().insertProduct(productList.get(i));
                            }

                            unitList = response1.getUnits();
                            for (int i = 0; i < unitList.size(); i++) {
                                room_db.unitDao().insertUnit(unitList.get(i));
                            }



                            Toastie.allCustom(AddEditProductActivity.this)
                                    .setTypeFace(Typeface.DEFAULT_BOLD)
                                    .setTextSize(16)
                                    .setCardRadius(25)
                                    .setCardElevation(10)
                                    .setIcon(R.drawable.ic_check_circle_black_24dp)
                                    .setCardBackgroundColor(R.color.purple_500)
                                    .setMessage(response1.getMessage())
                                    .setGravity(Gravity.BOTTOM, 5, 5)
                                    .createToast(Toast.LENGTH_LONG)
                                    .show();

                        } else {
                            Toastie.allCustom(AddEditProductActivity.this)
                                    .setTypeFace(Typeface.DEFAULT_BOLD)
                                    .setTextSize(16)
                                    .setCardRadius(25)
                                    .setCardElevation(10)
                                    .setIcon(R.drawable.ic_error_black_24dp)
                                    .setCardBackgroundColor(R.color.red)
                                    .setMessage(response1.getMessage())
                                    .setGravity(Gravity.BOTTOM, 5, 5)
                                    .createToast(Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toastie.allCustom(AddEditProductActivity.this)
                                .setTypeFace(Typeface.DEFAULT_BOLD)
                                .setTextSize(16)
                                .setCardRadius(25)
                                .setCardElevation(10)
                                .setIcon(R.drawable.ic_error_black_24dp)
                                .setCardBackgroundColor(R.color.red)
                                .setMessage(response1.getMessage())
                                .setGravity(Gravity.BOTTOM, 5, 5)
                                .createToast(Toast.LENGTH_LONG)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<AllDataResponse> call, Throwable t) {
                    Toastie.allCustom(AddEditProductActivity.this)
                            .setTypeFace(Typeface.DEFAULT_BOLD)
                            .setTextSize(16)
                            .setCardRadius(25)
                            .setCardElevation(10)
                            .setIcon(R.drawable.ic_error_black_24dp)
                            .setCardBackgroundColor(R.color.red)
                            .setMessage("No server response!")
                            .setGravity(Gravity.BOTTOM, 5, 5)
                            .createToast(Toast.LENGTH_LONG)
                            .show();
                    progressDialog.closeDialog();
                }
            });
        }
    }


    public void deleteProduct(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure to delete product? All related info will be lost");
        builder1.setTitle("Warning");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    if (product_id != -1) {
                        progressDialog.showDialog("Deleting...");

                        if (checkInternet.isInternetConnected(AddEditProductActivity.this)) {
                            call = RetrofitClient.getInstance().getApi().deleteProduct(product_id);
                            call.enqueue(new Callback<AllDataResponse>() {
                                @Override
                                public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                                    AllDataResponse response1 = response.body();
                                    progressDialog.closeDialog();
                                    if (response1 != null) {
                                        if (!response1.isError()) {
                                            room_db.clearAllTables();

                                            userList = response1.getUsers();
                                            for (int i = 0; i < userList.size(); i++) {
                                                room_db.userDao().insertUser(userList.get(i));
                                            }

                                            businessInfoList = response1.getBusiness_info();
                                            for (int i = 0; i < businessInfoList.size(); i++) {
                                                room_db.businessInfoDao().insertBusinessInfo(businessInfoList.get(i));
                                            }

                                            categoryList = response1.getCategories();
                                            for (int i = 0; i < categoryList.size(); i++) {
                                                room_db.categoryDao().insertCategory(categoryList.get(i));
                                            }

                                            productList = response1.getProducts();
                                            for (int i = 0; i < productList.size(); i++) {
                                                room_db.productDao().insertProduct(productList.get(i));
                                            }

                                            unitList = response1.getUnits();
                                            for (int i = 0; i < unitList.size(); i++) {
                                                room_db.unitDao().insertUnit(unitList.get(i));
                                            }

                                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                finish();
                                            }, 500);

                                        } else {


                                            Toastie.allCustom(AddEditProductActivity.this)
                                                    .setTypeFace(Typeface.DEFAULT_BOLD)
                                                    .setTextSize(16)
                                                    .setCardRadius(25)
                                                    .setCardElevation(10)
                                                    .setIcon(R.drawable.ic_error_black_24dp)
                                                    .setCardBackgroundColor(R.color.red)
                                                    .setMessage(response1.getMessage())
                                                    .setGravity(Gravity.BOTTOM, 5, 5)
                                                    .createToast(Toast.LENGTH_LONG)
                                                    .show();
//                        Toast.makeText(SplashscreenActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toastie.allCustom(AddEditProductActivity.this)
                                                .setTypeFace(Typeface.DEFAULT_BOLD)
                                                .setTextSize(16)
                                                .setCardRadius(25)
                                                .setCardElevation(10)
                                                .setIcon(R.drawable.ic_error_black_24dp)
                                                .setCardBackgroundColor(R.color.red)
                                                .setMessage("No server reposonse!")
                                                .setGravity(Gravity.BOTTOM, 5, 5)
                                                .createToast(Toast.LENGTH_LONG)
                                                .show();

                                    }

                                }

                                @Override
                                public void onFailure(Call<AllDataResponse> call, Throwable t) {
                                    progressDialog.closeDialog();
                                    Log.e("error", "Connection problem or " + t.getMessage());
                                    Toastie.allCustom(AddEditProductActivity.this)
                                            .setTypeFace(Typeface.DEFAULT_BOLD)
                                            .setTextSize(16)
                                            .setCardRadius(25)
                                            .setCardElevation(10)
                                            .setIcon(R.drawable.ic_error_black_24dp)
                                            .setCardBackgroundColor(R.color.red)
                                            .setMessage("Connection problem or " + t.getMessage())
                                            .setGravity(Gravity.BOTTOM, 5, 5)
                                            .createToast(Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        } else {
                            checkInternet.showInternetDialog(AddEditProductActivity.this);
                        }
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getResources().getColor(R.color.purple_500));


    }


    public void addProductServer(View view) {

        if (category_id == -1) {
            textInputLayoutCategory.setError("Required | select from dropdown");
            textInputLayoutCategory.setErrorEnabled(true);
            return;
        } else {
            textInputLayoutCategory.setError(null);
            textInputLayoutCategory.setErrorEnabled(false);
        }

        if (unit_id == -1) {
            textInputLayoutUnit.setError("Required | select from dropdown");
            textInputLayoutUnit.setErrorEnabled(true);
            return;
        } else {
            textInputLayoutUnit.setError(null);
            textInputLayoutUnit.setErrorEnabled(false);
        }

        if (checkInternet.isInternetConnected(this)) {
            if (validateField(textInputLayoutProName) && validateField(textInputLayoutQty) && validateField(textInputLayoutPrice) && validateField(textInputLayoutThreshold)) {
                String notes = "-";
                if (!textInputLayoutDesc.getEditText().getText().toString().isEmpty()) {
                    notes = textInputLayoutDesc.getEditText().getText().toString();
                }
                String product_name = textInputLayoutProName.getEditText().getText().toString();
                String product_price = textInputLayoutPrice.getEditText().getText().toString();
                String product_threshold = textInputLayoutThreshold.getEditText().getText().toString();
                String qty = textInputLayoutQty.getEditText().getText().toString();

                if (product_id == -1) {
                    progressDialog.showDialog("Adding...");

                    if (imgFile == null) {
                        call = RetrofitClient.getInstance().getApi().addProduct(user_id, category_id, unit_id,product_name, product_price, qty, product_threshold, notes);
                        call.enqueue(new Callback<AllDataResponse>() {
                            @Override
                            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                                AllDataResponse response1 = response.body();
                                progressDialog.closeDialog();
                                if (response1 != null) {
                                    if (!response1.isError()) {
                                        room_db.clearAllTables();

                                        userList = response1.getUsers();
                                        for (int i = 0; i < userList.size(); i++) {
                                            room_db.userDao().insertUser(userList.get(i));
                                        }

                                        businessInfoList = response1.getBusiness_info();
                                        for (int i = 0; i < businessInfoList.size(); i++) {
                                            room_db.businessInfoDao().insertBusinessInfo(businessInfoList.get(i));
                                        }

                                        categoryList = response1.getCategories();
                                        for (int i = 0; i < categoryList.size(); i++) {
                                            room_db.categoryDao().insertCategory(categoryList.get(i));
                                        }

                                        productList = response1.getProducts();
                                        for (int i = 0; i < productList.size(); i++) {
                                            room_db.productDao().insertProduct(productList.get(i));
                                        }

                                        unitList = response1.getUnits();
                                        for (int i = 0; i < unitList.size(); i++) {
                                            room_db.unitDao().insertUnit(unitList.get(i));
                                        }

                                        textInputLayoutProName.getEditText().setText("");
                                        textInputLayoutPrice.getEditText().setText("");
                                        textInputLayoutThreshold.getEditText().setText("");
                                        textInputLayoutQty.getEditText().setText("");
                                        textInputLayoutDesc.getEditText().setText("");
                                        category_id = -1;
                                        unit_id = -1;
                                        acTextViewCategory.setText("");
                                        acTextViewUnit.setText("");
                                        Toastie.allCustom(AddEditProductActivity.this)
                                                .setTypeFace(Typeface.DEFAULT_BOLD)
                                                .setTextSize(16)
                                                .setCardRadius(25)
                                                .setCardElevation(10)
                                                .setIcon(R.drawable.ic_check_circle_black_24dp)
                                                .setCardBackgroundColor(R.color.purple_500)
                                                .setMessage(response1.getMessage())
                                                .setGravity(Gravity.BOTTOM, 5, 5)
                                                .createToast(Toast.LENGTH_LONG)
                                                .show();

                                    } else {


                                        Toastie.allCustom(AddEditProductActivity.this)
                                                .setTypeFace(Typeface.DEFAULT_BOLD)
                                                .setTextSize(16)
                                                .setCardRadius(25)
                                                .setCardElevation(10)
                                                .setIcon(R.drawable.ic_error_black_24dp)
                                                .setCardBackgroundColor(R.color.red)
                                                .setMessage(response1.getMessage())
                                                .setGravity(Gravity.BOTTOM, 5, 5)
                                                .createToast(Toast.LENGTH_LONG)
                                                .show();
//                        Toast.makeText(SplashscreenActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toastie.allCustom(AddEditProductActivity.this)
                                            .setTypeFace(Typeface.DEFAULT_BOLD)
                                            .setTextSize(16)
                                            .setCardRadius(25)
                                            .setCardElevation(10)
                                            .setIcon(R.drawable.ic_error_black_24dp)
                                            .setCardBackgroundColor(R.color.red)
                                            .setMessage("No server reposonse!")
                                            .setGravity(Gravity.BOTTOM, 5, 5)
                                            .createToast(Toast.LENGTH_LONG)
                                            .show();

                                }

                            }

                            @Override
                            public void onFailure(Call<AllDataResponse> call, Throwable t) {
                                progressDialog.closeDialog();
                                Log.e("error", "Connection problem or " + t.getMessage());
                                Toastie.allCustom(AddEditProductActivity.this)
                                        .setTypeFace(Typeface.DEFAULT_BOLD)
                                        .setTextSize(16)
                                        .setCardRadius(25)
                                        .setCardElevation(10)
                                        .setIcon(R.drawable.ic_error_black_24dp)
                                        .setCardBackgroundColor(R.color.red)
                                        .setMessage("Connection problem or " + t.getMessage())
                                        .setGravity(Gravity.BOTTOM, 5, 5)
                                        .createToast(Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    } else {
                        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), imgFile);
                        MultipartBody.Part partImage = MultipartBody.Part.createFormData("file", imgFile.getName(), reqBody);
                        call = RetrofitClient.getInstance().getApi().addProductWithPic(partImage, user_id, category_id, unit_id,product_name, product_price, qty, product_threshold, notes);
                        call.enqueue(new Callback<AllDataResponse>() {

                            @Override
                            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                                AllDataResponse response1 = response.body();
                                progressDialog.closeDialog();
                                if (response1 != null) {
                                    if (!response1.isError()) {
                                        room_db.clearAllTables();

                                        userList = response1.getUsers();
                                        for (int i = 0; i < userList.size(); i++) {
                                            room_db.userDao().insertUser(userList.get(i));
                                        }

                                        businessInfoList = response1.getBusiness_info();
                                        for (int i = 0; i < businessInfoList.size(); i++) {
                                            room_db.businessInfoDao().insertBusinessInfo(businessInfoList.get(i));
                                        }

                                        categoryList = response1.getCategories();
                                        for (int i = 0; i < categoryList.size(); i++) {
                                            room_db.categoryDao().insertCategory(categoryList.get(i));
                                        }

                                        productList = response1.getProducts();
                                        for (int i = 0; i < productList.size(); i++) {
                                            room_db.productDao().insertProduct(productList.get(i));
                                        }

                                        unitList = response1.getUnits();
                                        for (int i = 0; i < unitList.size(); i++) {
                                            room_db.unitDao().insertUnit(unitList.get(i));
                                        }

                                        textInputLayoutProName.getEditText().setText("");
                                        textInputLayoutPrice.getEditText().setText("");
                                        textInputLayoutThreshold.getEditText().setText("");
                                        textInputLayoutQty.getEditText().setText("");
                                        textInputLayoutDesc.getEditText().setText("");
                                        category_id = -1;
                                        unit_id = -1;
                                        acTextViewCategory.setText("");
                                        acTextViewUnit.setText("");
                                        Toastie.allCustom(AddEditProductActivity.this)
                                                .setTypeFace(Typeface.DEFAULT_BOLD)
                                                .setTextSize(16)
                                                .setCardRadius(25)
                                                .setCardElevation(10)
                                                .setIcon(R.drawable.ic_check_circle_black_24dp)
                                                .setCardBackgroundColor(R.color.purple_500)
                                                .setMessage(response1.getMessage())
                                                .setGravity(Gravity.BOTTOM, 5, 5)
                                                .createToast(Toast.LENGTH_LONG)
                                                .show();

                                    } else {


                                        Toastie.allCustom(AddEditProductActivity.this)
                                                .setTypeFace(Typeface.DEFAULT_BOLD)
                                                .setTextSize(16)
                                                .setCardRadius(25)
                                                .setCardElevation(10)
                                                .setIcon(R.drawable.ic_error_black_24dp)
                                                .setCardBackgroundColor(R.color.red)
                                                .setMessage(response1.getMessage())
                                                .setGravity(Gravity.BOTTOM, 5, 5)
                                                .createToast(Toast.LENGTH_LONG)
                                                .show();
//                        Toast.makeText(SplashscreenActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toastie.allCustom(AddEditProductActivity.this)
                                            .setTypeFace(Typeface.DEFAULT_BOLD)
                                            .setTextSize(16)
                                            .setCardRadius(25)
                                            .setCardElevation(10)
                                            .setIcon(R.drawable.ic_error_black_24dp)
                                            .setCardBackgroundColor(R.color.red)
                                            .setMessage("No server reposonse!")
                                            .setGravity(Gravity.BOTTOM, 5, 5)
                                            .createToast(Toast.LENGTH_LONG)
                                            .show();

                                }

                            }

                            @Override
                            public void onFailure(Call<AllDataResponse> call, Throwable t) {
                                progressDialog.closeDialog();
                                Log.e("error", "Connection problem or " + t.getMessage());
                                Toastie.allCustom(AddEditProductActivity.this)
                                        .setTypeFace(Typeface.DEFAULT_BOLD)
                                        .setTextSize(16)
                                        .setCardRadius(25)
                                        .setCardElevation(10)
                                        .setIcon(R.drawable.ic_error_black_24dp)
                                        .setCardBackgroundColor(R.color.red)
                                        .setMessage("Connection problem or " + t.getMessage())
                                        .setGravity(Gravity.BOTTOM, 5, 5)
                                        .createToast(Toast.LENGTH_LONG)
                                        .show();
                            }

                        });
                    }
                } else {
                    progressDialog.showDialog("Updating...");
                    if (imgFile == null) {
                        call = RetrofitClient.getInstance().getApi().editProduct(product_id, category_id, unit_id,product_name, product_price, qty, product_threshold, notes);
                        call.enqueue(new Callback<AllDataResponse>() {
                            @Override
                            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                                AllDataResponse response1 = response.body();
                                progressDialog.closeDialog();
                                if (response1 != null) {
                                    if (!response1.isError()) {
                                        room_db.clearAllTables();

                                        userList = response1.getUsers();
                                        for (int i = 0; i < userList.size(); i++) {
                                            room_db.userDao().insertUser(userList.get(i));
                                        }

                                        businessInfoList = response1.getBusiness_info();
                                        for (int i = 0; i < businessInfoList.size(); i++) {
                                            room_db.businessInfoDao().insertBusinessInfo(businessInfoList.get(i));
                                        }

                                        categoryList = response1.getCategories();
                                        for (int i = 0; i < categoryList.size(); i++) {
                                            room_db.categoryDao().insertCategory(categoryList.get(i));
                                        }

                                        productList = response1.getProducts();
                                        for (int i = 0; i < productList.size(); i++) {
                                            room_db.productDao().insertProduct(productList.get(i));
                                        }

                                        unitList = response1.getUnits();
                                        for (int i = 0; i < unitList.size(); i++) {
                                            room_db.unitDao().insertUnit(unitList.get(i));
                                        }

                                        textInputLayoutProName.getEditText().setText("");
                                        textInputLayoutPrice.getEditText().setText("");
                                        textInputLayoutThreshold.getEditText().setText("");
                                        textInputLayoutQty.getEditText().setText("");
                                        textInputLayoutDesc.getEditText().setText("");
                                        category_id = -1;
                                        unit_id = -1;
                                        acTextViewCategory.setText("");
                                        acTextViewUnit.setText("");
                                        Toastie.allCustom(AddEditProductActivity.this)
                                                .setTypeFace(Typeface.DEFAULT_BOLD)
                                                .setTextSize(16)
                                                .setCardRadius(25)
                                                .setCardElevation(10)
                                                .setIcon(R.drawable.ic_check_circle_black_24dp)
                                                .setCardBackgroundColor(R.color.purple_500)
                                                .setMessage(response1.getMessage())
                                                .setGravity(Gravity.BOTTOM, 5, 5)
                                                .createToast(Toast.LENGTH_LONG)
                                                .show();

                                    } else {


                                        Toastie.allCustom(AddEditProductActivity.this)
                                                .setTypeFace(Typeface.DEFAULT_BOLD)
                                                .setTextSize(16)
                                                .setCardRadius(25)
                                                .setCardElevation(10)
                                                .setIcon(R.drawable.ic_error_black_24dp)
                                                .setCardBackgroundColor(R.color.red)
                                                .setMessage(response1.getMessage())
                                                .setGravity(Gravity.BOTTOM, 5, 5)
                                                .createToast(Toast.LENGTH_LONG)
                                                .show();
//                        Toast.makeText(SplashscreenActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toastie.allCustom(AddEditProductActivity.this)
                                            .setTypeFace(Typeface.DEFAULT_BOLD)
                                            .setTextSize(16)
                                            .setCardRadius(25)
                                            .setCardElevation(10)
                                            .setIcon(R.drawable.ic_error_black_24dp)
                                            .setCardBackgroundColor(R.color.red)
                                            .setMessage("No server reposonse!")
                                            .setGravity(Gravity.BOTTOM, 5, 5)
                                            .createToast(Toast.LENGTH_LONG)
                                            .show();

                                }

                            }

                            @Override
                            public void onFailure(Call<AllDataResponse> call, Throwable t) {
                                progressDialog.closeDialog();
                                Log.e("error", "Connection problem or " + t.getMessage());
                                Toastie.allCustom(AddEditProductActivity.this)
                                        .setTypeFace(Typeface.DEFAULT_BOLD)
                                        .setTextSize(16)
                                        .setCardRadius(25)
                                        .setCardElevation(10)
                                        .setIcon(R.drawable.ic_error_black_24dp)
                                        .setCardBackgroundColor(R.color.red)
                                        .setMessage("Connection problem or " + t.getMessage())
                                        .setGravity(Gravity.BOTTOM, 5, 5)
                                        .createToast(Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    } else {
                        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), imgFile);
                        MultipartBody.Part partImage = MultipartBody.Part.createFormData("file", imgFile.getName(), reqBody);
                        call = RetrofitClient.getInstance().getApi().editProductWithPic(partImage, product_id, category_id, unit_id,product_name, product_price, qty, product_threshold, notes);
                        call.enqueue(new Callback<AllDataResponse>() {
                            @Override
                            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                                AllDataResponse response1 = response.body();
                                progressDialog.closeDialog();
                                if (response1 != null) {
                                    if (!response1.isError()) {
                                        room_db.clearAllTables();

                                        userList = response1.getUsers();
                                        for (int i = 0; i < userList.size(); i++) {
                                            room_db.userDao().insertUser(userList.get(i));
                                        }

                                        businessInfoList = response1.getBusiness_info();
                                        for (int i = 0; i < businessInfoList.size(); i++) {
                                            room_db.businessInfoDao().insertBusinessInfo(businessInfoList.get(i));
                                        }

                                        categoryList = response1.getCategories();
                                        for (int i = 0; i < categoryList.size(); i++) {
                                            room_db.categoryDao().insertCategory(categoryList.get(i));
                                        }

                                        productList = response1.getProducts();
                                        for (int i = 0; i < productList.size(); i++) {
                                            room_db.productDao().insertProduct(productList.get(i));
                                        }

                                        unitList = response1.getUnits();
                                        for (int i = 0; i < unitList.size(); i++) {
                                            room_db.unitDao().insertUnit(unitList.get(i));
                                        }

                                        textInputLayoutProName.getEditText().setText("");
                                        textInputLayoutPrice.getEditText().setText("");
                                        textInputLayoutThreshold.getEditText().setText("");
                                        textInputLayoutQty.getEditText().setText("");
                                        textInputLayoutDesc.getEditText().setText("");
                                        category_id = -1;
                                        unit_id = -1;
                                        acTextViewCategory.setText("");
                                        acTextViewUnit.setText("");

                                        Toastie.allCustom(AddEditProductActivity.this)
                                                .setTypeFace(Typeface.DEFAULT_BOLD)
                                                .setTextSize(16)
                                                .setCardRadius(25)
                                                .setCardElevation(10)
                                                .setIcon(R.drawable.ic_check_circle_black_24dp)
                                                .setCardBackgroundColor(R.color.purple_500)
                                                .setMessage(response1.getMessage())
                                                .setGravity(Gravity.BOTTOM, 5, 5)
                                                .createToast(Toast.LENGTH_LONG)
                                                .show();

                                    } else {


                                        Toastie.allCustom(AddEditProductActivity.this)
                                                .setTypeFace(Typeface.DEFAULT_BOLD)
                                                .setTextSize(16)
                                                .setCardRadius(25)
                                                .setCardElevation(10)
                                                .setIcon(R.drawable.ic_error_black_24dp)
                                                .setCardBackgroundColor(R.color.red)
                                                .setMessage(response1.getMessage())
                                                .setGravity(Gravity.BOTTOM, 5, 5)
                                                .createToast(Toast.LENGTH_LONG)
                                                .show();
//                        Toast.makeText(SplashscreenActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toastie.allCustom(AddEditProductActivity.this)
                                            .setTypeFace(Typeface.DEFAULT_BOLD)
                                            .setTextSize(16)
                                            .setCardRadius(25)
                                            .setCardElevation(10)
                                            .setIcon(R.drawable.ic_error_black_24dp)
                                            .setCardBackgroundColor(R.color.red)
                                            .setMessage("No server reposonse!")
                                            .setGravity(Gravity.BOTTOM, 5, 5)
                                            .createToast(Toast.LENGTH_LONG)
                                            .show();

                                }

                            }

                            @Override
                            public void onFailure(Call<AllDataResponse> call, Throwable t) {
                                progressDialog.closeDialog();
                                Log.e("error", "Connection problem or " + t.getMessage());
                                Toastie.allCustom(AddEditProductActivity.this)
                                        .setTypeFace(Typeface.DEFAULT_BOLD)
                                        .setTextSize(16)
                                        .setCardRadius(25)
                                        .setCardElevation(10)
                                        .setIcon(R.drawable.ic_error_black_24dp)
                                        .setCardBackgroundColor(R.color.red)
                                        .setMessage("Connection problem or " + t.getMessage())
                                        .setGravity(Gravity.BOTTOM, 5, 5)
                                        .createToast(Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }
                }


            }
        } else {
            checkInternet.showInternetDialog(this);
        }
    }

    public boolean validateField(TextInputLayout textInputLayout) {
        String email = textInputLayout.getEditText().getText().toString();

        if (email.isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("Fill field");
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setError(null);
            return true;


        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (call != null) {
            call.cancel();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }


}
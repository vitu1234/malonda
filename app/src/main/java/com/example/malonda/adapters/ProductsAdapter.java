package com.example.malonda.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.api.RetrofitClient;
import com.example.malonda.models.AllDataResponse;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.Product;
import com.example.malonda.models.Unit;
import com.example.malonda.models.User;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.supplier.activities.AddEditProductActivity;
import com.example.malonda.utils.CheckInternet;
import com.example.malonda.utils.MyProgressDialog;
import com.mrntlu.toastie.Toastie;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    Call<AllDataResponse> call;
    private List<User> userList;
    private List<Category> categoryList;
    private List<BusinessInfo> businessInfoList;
    private List<Product> productList;
    private List<Unit> unitList;
    CheckInternet checkInternet;
    MyProgressDialog progressDialog;
    AppDatabase room_db;
    

    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;


    public ProductsAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        room_db = AppDatabase.getDbInstance(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (viewType == SHOW_MENU) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_menu, parent, false);
            return new MenuViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_recycler_line, parent, false);
            return new MyViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Product entity = productList.get(position);
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textViewName.setText(productList.get(position).getProduct_name());
            ((MyViewHolder) holder).textViewCost.setText("Qty: " + productList.get(position).getQty());
            ((MyViewHolder) holder).textViewPrice.setText("K " + productList.get(position).getPrice());

            String imageUri = RetrofitClient.BASE_URL2 + "images/products/" + productList.get(position).getImg_url();

            Picasso.get().load(imageUri)
                    .placeholder(R.drawable.image_icon)
                    .error(R.drawable.image_icon)
                    .into(((MyViewHolder) holder).itemPic);

            ((MyViewHolder) holder).container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu(position);
                    return true;
                }
            });
        }

        if (holder instanceof MenuViewHolder) {
            ((MenuViewHolder) holder).textViewProductNameMenu.setText(productList.get(position).getProduct_name());
            ((MenuViewHolder) holder).textViewProductNameMenu.setVisibility(View.VISIBLE);
            //Menu Actions
            ((MenuViewHolder) holder).buttonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMenu();
                }
            });

            ((MenuViewHolder) holder).buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMenu();
                    Intent intent = new Intent(context, AddEditProductActivity.class);
                    intent.putExtra("product_id", productList.get(position).getProduct_id());
                    context.startActivity(intent);


                }
            });

            ((MenuViewHolder) holder).buttonDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Are you sure to delete "+productList.get(position).getProduct_name()+"? All related info will be lost");
                    builder1.setTitle("Warning");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteProduct(productList.get(position).getProduct_id(), position);
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
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red));
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.purple_700));


                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (productList.get(position).isShowMenu()) {
            return SHOW_MENU;
        } else {
            return HIDE_MENU;
        }
    }

    public void showMenu(int position) {
        for (int i = 0; i < productList.size(); i++) {
            productList.get(i).setShowMenu(false);
        }
        productList.get(position).setShowMenu(true);
        notifyDataSetChanged();
    }


    public boolean isMenuShown() {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).isShowMenu()) {
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for (int i = 0; i < productList.size(); i++) {
            productList.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }

    //deletes supplier from server
    private void deleteProduct(int product_id, int position) {
        checkInternet = new CheckInternet(context);
        progressDialog = new MyProgressDialog(context);
        room_db = AppDatabase.getDbInstance(context);
        progressDialog.showDialog("Deleting...");

        if (checkInternet.isInternetConnected(context)) {
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

                            productList.clear();
                            productList = response1.getProducts();
                            for (int i = 0; i < productList.size(); i++) {
                                room_db.productDao().insertProduct(productList.get(i));
                            }

                            unitList = response1.getUnits();
                            for (int i = 0; i < unitList.size(); i++) {
                                room_db.unitDao().insertUnit(unitList.get(i));
                            }


                            notifyDataSetChanged();

                            Toastie.allCustom(context)
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
                            Toastie.allCustom(context)
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
                        Toastie.allCustom(context)
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
                    Toastie.allCustom(context)
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
        } else {
            checkInternet.showInternetDialog(context);
        }
    }

    //filter list'
    // method for filtering our recyclerview items.
    public void filterList(List<Product> productList) {
        // below line is to add our filtered
        // list in our course array list.
        this.productList = productList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }


    public void swapItems(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textViewName, textViewCost, textViewPrice;
        RelativeLayout container;

        CircleImageView itemPic;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.frameProduct);
            textViewName = itemView.findViewById(R.id.item_name);
            itemPic = itemView.findViewById(R.id.item_picture);
            textViewCost = itemView.findViewById(R.id.item_qty);
            textViewPrice = itemView.findViewById(R.id.item_price);

            itemView.setOnLongClickListener(this);

        }


        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }


    //Our menu view
    public class MenuViewHolder extends RecyclerView.ViewHolder {

        ImageView buttonClose, buttonEdit, buttonDel;
        TextView textViewProductNameMenu;

        public MenuViewHolder(View view) {
            super(view);
            buttonClose = view.findViewById(R.id.closeBtn);
            buttonEdit = view.findViewById(R.id.editCategoryBtn);
            buttonDel = view.findViewById(R.id.delCategoryBtn);

            textViewProductNameMenu = view.findViewById(R.id.categoryNameMenu);
            textViewProductNameMenu.setVisibility(View.INVISIBLE);
        }
    }
}


package com.example.malonda.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.api.RetrofitClient;
import com.example.malonda.buyer.Fragments.BuyerDashboardFragment;
import com.example.malonda.buyer.activities.ProductDetailsActivity;
import com.example.malonda.models.AllDataResponse;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.POS;
import com.example.malonda.models.Product;
import com.example.malonda.models.Unit;
import com.example.malonda.models.User;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.utils.CheckInternet;
import com.example.malonda.utils.MyProgressDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;

public class TerminalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    AlertDialog alertDialog;
    int cust_qty = 1;

    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    BuyerDashboardFragment posTerminalFragment;

    public TerminalAdapter(BuyerDashboardFragment posTerminalFragment, Context context, List<Product> productList) {
        this.context = posTerminalFragment.getContext();
        this.productList = productList;
        this.posTerminalFragment = posTerminalFragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;


        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_grid_product, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Product entity = productList.get(position);
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textViewName.setText(productList.get(position).getProduct_name());
            ((MyViewHolder) holder).textViewPrice.setText("K " + productList.get(position).getPrice());

            String imageUri = RetrofitClient.BASE_URL2 + "images/products/" + productList.get(position).getImg_url();

            Picasso.get().load(imageUri)
                    .placeholder(R.drawable.image_icon)
                    .error(R.drawable.image_icon)
                    .into(((MyViewHolder) holder).itemPic);

            ((MyViewHolder) holder).container.setOnLongClickListener(v -> {
//                    showMenu(position);
                viewProductQtyDetails(position);

                return true;
            });

            ((MyViewHolder) holder).container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    room_db = AppDatabase.getDbInstance(context);
                    Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(100);

                    Intent intent = new Intent(context.getApplicationContext(), ProductDetailsActivity.class);
                    intent.putExtra("product_id",productList.get(position).getProduct_id());
                    context.startActivity(intent);


//                    POS pos = new POS();
//                    pos.setProduct_id(productList.get(position).getProduct_id());
//                    pos.setQty(Integer.parseInt(1+""));
//                    pos.setTotal((double) Integer.parseInt(productList.get(position).getPrice()) );
//                    room_db.posDao().insertPos(pos);
//
//                    double total = 0;
//                    List<POS> posList = room_db.posDao().getAllPos();
//                    for (int i = 0; i < posList.size(); i++) {
//                        total += posList.get(i).getTotal();
//                    }
//
//                    posTerminalFragment.textViewTotalItems.setText("Items: K" + total);
//                    posTerminalFragment.buttonDiscard.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    private void viewProductQtyDetails(int position) {
        TextView textViewprodNameTitle, textViewQtyStock, textViewCusQty, textViewPrice;
        Button buttonAddQty, buttonReduceQty;
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        progressDialog = new MyProgressDialog(context);
        room_db = AppDatabase.getDbInstance(context);

        View promptsView = li.inflate(R.layout.product_qty_details_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);


        textViewprodNameTitle = promptsView.findViewById(R.id.prodPromptTitle);
        textViewQtyStock = promptsView.findViewById(R.id.prodPromptStock);
        textViewCusQty = promptsView.findViewById(R.id.prodPromptQty);
        textViewPrice = promptsView.findViewById(R.id.prodPromptPrice);
        buttonReduceQty = promptsView.findViewById(R.id.button2f);
        buttonAddQty = promptsView.findViewById(R.id.button3);

        textViewprodNameTitle.setText(productList.get(position).getProduct_name());
        textViewQtyStock.setText(productList.get(position).getQty() + "");
        textViewPrice.setText("K" + (double) Integer.parseInt(productList.get(position).getPrice()));


        textViewCusQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int new_qty = Integer.parseInt(s.toString());
                if (new_qty > Integer.parseInt(String.valueOf(productList.get(position).getQty()))) {
                    progressDialog.showErrorToast("The requested quantiy is greater than remaining stock!");
                } else {
                    cust_qty = new_qty;
                    double new_price = (double) Integer.parseInt(productList.get(position).getPrice()) * new_qty;
                    textViewPrice.setText("K" + new_price + "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonAddQty.setOnClickListener(v -> {
            int qty = Integer.parseInt(textViewCusQty.getText().toString());
            if (qty < Integer.parseInt(String.valueOf(productList.get(position).getQty()))) {
                textViewCusQty.setText(qty + 1 + "");
            } else {
                progressDialog.showErrorToast("The requested quantity is greater than remaining stock!");
            }

        });

        buttonReduceQty.setOnClickListener(v -> {
            int qty = Integer.parseInt(textViewCusQty.getText().toString());
            int new_qty = qty - 1;

            if (new_qty == 0) {
                textViewCusQty.setText("1");
            } else {
                textViewCusQty.setText(new_qty + "");
            }
        });


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",
                        (dialog, id) -> {
                            //save here
                        })
                .setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.purple_500));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            /*if (room_db.posDao().getSinglePosCount(productList.get(position).getProduct_id()) > 0) {
                room_db.posDao().deletePosByProdID(productList.get(position).getProduct_id());
            }*/
            POS pos = new POS();
            pos.setProduct_id(productList.get(position).getProduct_id());
            pos.setQty(Integer.parseInt(textViewCusQty.getText().toString()));
            pos.setTotal((double) Integer.parseInt(productList.get(position).getPrice()) * Integer.parseInt(textViewCusQty.getText().toString()));
            room_db.posDao().insertPos(pos);

            double total = 0;
            List<POS> posList = room_db.posDao().getAllPos();
            for (int i = 0; i < posList.size(); i++) {
                total += posList.get(i).getTotal();
            }

            posTerminalFragment.textViewTotalItems.setText("Items: K" + total);
            posTerminalFragment.buttonDiscard.setVisibility(View.VISIBLE);
            alertDialog.dismiss();

        });
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

    public void sortList(List<Product> productList) {
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

        TextView textViewName, textViewPrice;
        CardView container;

        ImageView itemPic;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.cardView);
            textViewName = itemView.findViewById(R.id.cardProdName);
            itemPic = itemView.findViewById(R.id.cardProdImage);
            textViewPrice = itemView.findViewById(R.id.cardProdPrice);

            itemView.setOnLongClickListener(this);

        }


        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }


}

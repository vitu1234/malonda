package com.example.malonda.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.supplier.activities.SalesCheckoutActivity;
import com.example.malonda.models.POS;
import com.example.malonda.models.Product;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.utils.MyProgressDialog;

import java.util.List;

public class SaleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    private List<POS> posList;
    AppDatabase room_db;
    MyProgressDialog progressDialog;
    AlertDialog alertDialog;

    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;
    SalesCheckoutActivity salesReceiptCheckoutActivity;

    public SaleAdapter(SalesCheckoutActivity salesReceiptCheckoutActivity, Context context, List<POS> posList) {
        this.context = context;
        this.posList = posList;
        this.salesReceiptCheckoutActivity = salesReceiptCheckoutActivity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (viewType == SHOW_MENU) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_menu, parent, false);
            return new MenuViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_recycler_line, parent, false);
            return new MyViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        room_db = AppDatabase.getDbInstance(context);
        POS pos = posList.get(position);
        Product entity = room_db.productDao().findByProductId(pos.getProduct_id());
        int qty = room_db.posDao().totalProQtyPosCount(pos.getProduct_id());

        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textViewName.setText(entity.getProduct_name());
            ((MyViewHolder) holder).textViewQty.setText("" + qty);
            ((MyViewHolder) holder).textViewPrice.setText("K " + entity.getPrice());
            ((MyViewHolder) holder).textViewTotal.setText("K " + (qty * (Integer.parseInt(entity.getPrice()))));

            ((MyViewHolder) holder).container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu(position);
                    return true;
                }
            });
        }

        if (holder instanceof MenuViewHolder) {
            ((MenuViewHolder) holder).textViewProductNameMenu.setText(entity.getProduct_name());
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
                    viewProductQtyDetails(position);

                }
            });

            ((MenuViewHolder) holder).buttonDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Are you sure to delete " + entity.getProduct_name() + " from the list?");
                    builder1.setTitle("Warning");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteProduct(posList.get(position).getProduct_id(), position);
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
        return posList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (posList.get(position).isShowMenu()) {
            return SHOW_MENU;
        } else {
            return HIDE_MENU;
        }
    }

    public void showMenu(int position) {
        for (int i = 0; i < posList.size(); i++) {
            posList.get(i).setShowMenu(false);
        }
        posList.get(position).setShowMenu(true);
        notifyDataSetChanged();
    }


    public boolean isMenuShown() {
        for (int i = 0; i < posList.size(); i++) {
            if (posList.get(i).isShowMenu()) {
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for (int i = 0; i < posList.size(); i++) {
            posList.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }

    //deletes pos item
    private void deleteProduct(int product_id, int position) {
        room_db = AppDatabase.getDbInstance(context);
        room_db.posDao().deletePosByProdID(product_id);
        posList.remove(position);
        notifyItemRemoved(position);

        this.posList = room_db.posDao().getAllPosGrouped();
        notifyDataSetChanged();
        salesReceiptCheckoutActivity.setViews();
    }

    //filter list'
    // method for filtering our recyclerview items.
    public void filterList(List<POS> posList) {
        // below line is to add our filtered
        // list in our course array list.
        this.posList = posList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }


    public void swapItems(List<POS> posList) {
        this.posList = posList;
        notifyDataSetChanged();
    }

    private void viewProductQtyDetails(int position) {

        POS pos = posList.get(position);
        Product entity = room_db.productDao().findByProductId(pos.getProduct_id());
        int qty1 = room_db.posDao().totalProQtyPosCount(pos.getProduct_id());

        TextView textViewprodNameTitle, textViewQtyStock, textViewCusQty, textViewPrice;
        Button buttonAddQty, buttonReduceQty;
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        room_db = AppDatabase.getDbInstance(context);
        progressDialog = new MyProgressDialog(context);
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

        textViewprodNameTitle.setText(entity.getProduct_name());
        textViewQtyStock.setText(entity.getQty() + "");
        textViewPrice.setText("K" + (qty1 * (Integer.parseInt(entity.getPrice()))));


        textViewCusQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int new_qty = Integer.parseInt(s.toString());
                if (new_qty > Integer.parseInt(String.valueOf(entity.getQty()))) {
                    progressDialog.showErrorToast("The requested quantiy is greater than remaining stock!");
                } else {
//                    cust_qty = new_qty;
                    double new_price = (double) Integer.parseInt(entity.getPrice()) * new_qty;
                    textViewPrice.setText("K" + new_price + "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonAddQty.setOnClickListener(v -> {
            int qty = Integer.parseInt(textViewCusQty.getText().toString());
            if (qty < Integer.parseInt(String.valueOf(entity.getQty()))) {
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
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.purple_700));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            /*if (room_db.posDao().getSinglePosCount(productList.get(position).getProduct_id()) > 0) {
                room_db.posDao().deletePosByProdID(productList.get(position).getProduct_id());
            }*/
            room_db.posDao().deletePosByProdID(pos.getProduct_id());
            POS pos1 = new POS();
            pos1.setProduct_id(entity.getProduct_id());
            pos1.setQty(Integer.parseInt(textViewCusQty.getText().toString()));
            pos1.setTotal((double) Integer.parseInt(entity.getPrice()) * Integer.parseInt(textViewCusQty.getText().toString()));
            room_db.posDao().insertPos(pos1);

            double total = 0;
            List<POS> posList = room_db.posDao().getAllPos();
            for (int i = 0; i < posList.size(); i++) {
                total += posList.get(i).getTotal();
            }

//            posTerminalFragment.textViewTotalItems.setText("Items: K" + total);
//            posTerminalFragment.buttonDiscard.setVisibility(View.VISIBLE);
            this.posList = room_db.posDao().getAllPosGrouped();
            notifyDataSetChanged();
            salesReceiptCheckoutActivity.setViews();
            alertDialog.dismiss();

        });
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textViewName, textViewQty, textViewPrice, textViewTotal;
        FrameLayout container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.frameSale);
            textViewName = itemView.findViewById(R.id.saleProdName);
            textViewQty = itemView.findViewById(R.id.saleProdQty);
            textViewPrice = itemView.findViewById(R.id.saleProdPrice);
            textViewTotal = itemView.findViewById(R.id.saleProdTotal);

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

//package com.iyuce.itoefl.Control.Main;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.github.barteksc.pdfviewer.PDFView;
//import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
//import com.iyuce.itoefl.R;
//import com.iyuce.itoefl.Utils.LogUtil;
//import com.iyuce.itoefl.Utils.PreferenceUtil;
//
///**
// * Created by LeBang on 2017/1/22
// */
//public class FragmentPdf extends Fragment implements OnPageChangeListener {
//
//    private int pdf_book_mark = 0;
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        PreferenceUtil.save(getActivity(), "pdf_book_mark", pdf_book_mark);
//    }
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_main_pdf, container, false);
//        initView(view);
//        return view;
//    }
//
//    private void initView(View view) {
//        PDFView mPdfView = (PDFView) view.findViewById(R.id.pdf_fragment);
//        mPdfView.fromAsset("sample.pdf")
//                .defaultPage(PreferenceUtil.getSharePre(getActivity()).getInt("pdf_book_mark", 0))
//                .enableSwipe(true)
//                .swipeHorizontal(false)
//                .enableDoubletap(true)
//                .onPageChange(this)
//                .load();
//    }
//
//    @Override
//    public void onPageChanged(int page, int pageCount) {
//        LogUtil.i("page = " + page + " , pageCount =" + pageCount);
//        pdf_book_mark = page;
//    }
//}
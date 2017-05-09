package com.products.qc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class QControlCaller extends Thread {
    Activity activity;
    ProgressBarDialogFragment cp;

    public QControlCaller(Activity activity){
        this.activity = activity;
    }

    public void run() {
        try {
            cp = new ProgressBarDialogFragment();
            cp.setCancelable(false);
            cp.show(activity.getFragmentManager(), "sendingdata");

            SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            String response;
            if (sharedPref.getInt(activity.getString(R.string.saved_saveqcontrol), 0) == 0) {
                response = connectToWebService("saveQControl", CreateQControl());
                editor.putInt(activity.getString(R.string.saved_saveqcontrol), 1);
                editor.putString(activity.getString(R.string.saved_responsecontrol), response);
                editor.apply();
            } else {
                response = sharedPref.getString(activity.getString(R.string.saved_responsecontrol), "");
            }
            boolean sendQcontroldetail = false,
                    sendQcontroldetailFactor = false,
                    sendQcontroldetailPicture = false;
            Cursor pallets = QueryRepository.getAllPallets(activity);
            for (int i = 0; i < pallets.getCount(); i++) {
                int palletId = pallets.getInt(pallets.getColumnIndexOrThrow(PalletReaderContract.PalletEntry.COLUMN_NAME_ENTRY_ID));
                int productId = QueryRepository.getProductIdByPalletId(activity, palletId);

                Cursor samplings = QueryRepository.getAllSamplingByPallet(activity, palletId);
                Cursor qcFactorTable = QueryRepository.getIcFactorsByProduct(activity, productId);
                for (int j = 0; j < samplings.getCount(); j++) {
                    int samplingId = samplings.getInt(samplings.getColumnIndexOrThrow(SamplingReaderContract.SamplingEntry.COLUMN_NAME_ENTRY_ID));
                    String qControlDetailString = CreateQControlDetail(response, pallets, samplings);
                    String response2 = "";
                    if (sendQcontroldetail || sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetail), -1) == -1) {
                        response2 = connectToWebService("saveQcontrolDetail", qControlDetailString);
                        editor.putInt(activity.getString(R.string.saved_saveqcontroldetail), samplingId);
                        editor.putString(activity.getString(R.string.saved_responsecontroldetail), response2);
                        editor.commit();
                        sendQcontroldetail = true;
                    } else if (sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetail), -1) == samplingId) {
                        response2 = sharedPref.getString(activity.getString(R.string.saved_responsecontroldetail), "");
                        editor.putInt(activity.getString(R.string.saved_saveqcontroldetail), -1);
                        editor.commit();
                        sendQcontroldetail = true;
                    }
                    int tableNumber = QueryRepository.getTableNumberBySamplingId(activity, samplingId);
                    Cursor qcFactorDataTable = QueryRepository.getQCFactorTablesByNumberAndSamplingId(activity, samplingId, tableNumber);
                    for (int k = 0; k < qcFactorDataTable.getCount(); k++) {
                        int qcFactorDataTableId = qcFactorDataTable.getInt(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataReaderContract.IcfactorDataEntry._ID));

                        if (sendQcontroldetailFactor || sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetailfactor), -1) == -1) {
                            editor.putInt(activity.getString(R.string.saved_saveqcontroldetailfactor), qcFactorDataTableId);
                            editor.commit();
                            String qControlDetailFactor = CreateQControlDetailFactor(response2, qcFactorDataTable, qcFactorTable);
                            String response3 = connectToWebService("saveQcontrolDetailFactor", qControlDetailFactor);
                            sendQcontroldetailFactor = true;
                        } else if (sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetailfactor), -1) == qcFactorDataTableId) {
                            editor.putInt(activity.getString(R.string.saved_saveqcontroldetailfactor), -1);
                            editor.commit();
                            sendQcontroldetailFactor = true;
                        }
                        qcFactorDataTable.moveToNext();
                    }
                    Cursor pictures = QueryRepository.getAllPictureBySampling(activity, samplingId);
                    for (int k = 0; k < pictures.getCount(); k++) {
                        int pictureId = pictures.getInt(pictures.getColumnIndexOrThrow(PictureReaderContract.PictureEntry.COLUMN_NAME_ENTRY_ID));

                        if (sendQcontroldetailPicture || sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetailpicture), -1) == -1) {
                            String picName = pictures.getString(pictures.getColumnIndexOrThrow(PictureReaderContract.PictureEntry.COLUMN_NAME_NAME));
                            String qControlDetailPicture = CreateQControlDetailPicture(response2, pictures);

                            Bitmap bitmap = scalePicture(picName);
                            String qPicture = CreatePicture(pictures, bitmap);
                            String response4 = connectToWebServicePicture("saveQcontrolDetailPicture", qControlDetailPicture, qPicture);
                            editor.putInt(activity.getString(R.string.saved_saveqcontroldetailpicture), pictureId);
                            editor.commit();
                            sendQcontroldetailPicture = true;
                        } else if (sharedPref.getInt(activity.getString(R.string.saved_saveqcontroldetailpicture), -1) == pictureId) {
                            editor.putInt(activity.getString(R.string.saved_saveqcontroldetailpicture), -1);
                            editor.commit();
                            sendQcontroldetailPicture = true;
                        }
                        pictures.moveToNext();
                    }
                    samplings.moveToNext();
                }
                pallets.moveToNext();
            }
            if (activity instanceof MainActivity) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        ((MainActivity) activity).menu.removeItem(R.id.action_send_data);
                        cp.dismiss();
                        ActionBarMethods.restart(activity);
                        Toast.makeText(activity, "Quality control successfully saved", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                AppConstant.restarting = true;
                AppConstant.dataSended = true;
                activity.finish();
            }
            MainActivity.rslt = response;
        } catch(Exception ex) {
            MainActivity.rslt=ex.toString();


            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Try again. Something was wrong", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public String connectToWebService(String OPERATION_NAME, String data){
        String SOAP_ACTION = "http://tempuri.org/" + OPERATION_NAME;
        String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSQservice/QCService.asmx";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("objInput");
        pi.setValue(data);
        pi.setType(String.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception) {
            cp.dismiss();
            response=exception.toString();
            ConnectionProblemDialogFragment cp = new ConnectionProblemDialogFragment(activity);
            cp.show(activity.getFragmentManager(), "connproblem");
            this.stop();
        }
        return response.toString();
    }

    public String connectToWebServicePicture(String OPERATION_NAME, String data, String picture){
        String SOAP_ACTION = "http://tempuri.org/" + OPERATION_NAME;
        String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        String SOAP_ADDRESS = "http://www.gmendez.net/WIP.WSQservice/QCService.asmx";

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("objInput");
        pi.setValue(data);
        pi.setType(String.class);
        request.addProperty(pi);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("str64Picture");
        pi2.setValue(picture);
        pi2.setType(String.class);
        request.addProperty(pi2);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
        }
        catch (Exception exception) {
            cp.dismiss();
            response=exception.toString();
            ConnectionProblemDialogFragment cp = new ConnectionProblemDialogFragment(activity);
            cp.show(activity.getFragmentManager(), "connproblem");
            this.stop();
        }
        return response.toString();
    }

    public String CreateQControl() {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        //Build first XML ************************************************************************************
        //***************************************************************************************************
        String beginDateString = sharedPref.getString(activity.getString(R.string.saved_begin_date), "");
        String finishDateString = sharedPref.getString(activity.getString(R.string.saved_finish_date), "");
        String incomingIdString = sharedPref.getString(activity.getString(R.string.saved_incoming_id), "");
        String manifestIdString = sharedPref.getString(activity.getString(R.string.saved_manifest_id), "");

        return "<Qcontrol>"+
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"+
                "<Id>0</Id>"+
                "<BeginDateTime>" + beginDateString.substring(0, 10) + "T" + beginDateString.substring(11, 19) + "</BeginDateTime>"+
                "<FinishDateTime>" + finishDateString.substring(0, 10) + "T" + finishDateString.substring(11, 19) + "</FinishDateTime>"+
                "<IncomingId>" + incomingIdString + "</IncomingId>"+
                "<ManifestId>" + manifestIdString + "</ManifestId>"+
                "</Qcontrol>";
    }

    public String CreateQControlDetail(String qControlId, Cursor pallet, Cursor sampling) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String tagIdString = String.valueOf(pallet.getInt(pallet.getColumnIndexOrThrow(PalletReaderContract.PalletEntry.COLUMN_NAME_CODE)));

        String temperatureString = String.valueOf(sampling.getFloat(sampling.getColumnIndexOrThrow(SamplingReaderContract.SamplingEntry.COLUMN_NAME_TEMPERATURE)));
        String growerString = sampling.getString(sampling.getColumnIndexOrThrow(SamplingReaderContract.SamplingEntry.COLUMN_NAME_GROWER));
        String plusString = String.valueOf(sampling.getInt(sampling.getColumnIndexOrThrow(SamplingReaderContract.SamplingEntry.COLUMN_NAME_PLUS)));

        String brixString = String.valueOf(sampling.getFloat(sampling.getColumnIndexOrThrow(SamplingReaderContract.SamplingEntry.COLUMN_NAME_BRIX)));
        String pressureString = String.valueOf(sampling.getFloat(sampling.getColumnIndexOrThrow(SamplingReaderContract.SamplingEntry.COLUMN_NAME_PRESSURE)));
        String measurementsString = String.valueOf(sampling.getInt(sampling.getColumnIndexOrThrow(SamplingReaderContract.SamplingEntry.COLUMN_NAME_MEASUREMENTS)));

        String beginDateString = sharedPref.getString(activity.getString(R.string.saved_begin_date), "");
        String finishDateString = sharedPref.getString(activity.getString(R.string.saved_finish_date), "");

        return "<QcontrolDetail>" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                "<Id>0</Id>" +
                "<QcontrolId>" + qControlId + "</QcontrolId>" +
                "<TagPallet>" + tagIdString + "</TagPallet>" +
                "<BeginDateTime>" + beginDateString.substring(0, 10) + "T" + beginDateString.substring(11, 19) + "</BeginDateTime>"+
                "<FinishDateTime>" + finishDateString.substring(0, 10) + "T" + finishDateString.substring(11, 19) + "</FinishDateTime>"+
                "<Temperature>" + temperatureString + "</Temperature>" +
                "<Grower>" + growerString + "</Grower>" +
                "<PlusPercent>" + plusString + "</PlusPercent>" +
                "<Brix>" + brixString + "</Brix>" +
                "<Pressure>" + pressureString + "</Pressure>" +
                "<Measurements>" + measurementsString + "</Measurements>" +
                "<QcontrolDetailFactorCollection />" +
                "<QcontrolDetailPictureCollection />" +
                "</QcontrolDetail>";
    }

    public String CreateQControlDetailFactor(String qControlDetailId, Cursor qcFactorDataTable, Cursor qcFactorTable) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String qCFactorDescriptionString = qcFactorDataTable.getString(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataReaderContract.IcfactorDataEntry.COLUMN_NAME_NAME));
        String qCFactorValueString = qcFactorDataTable.getString(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataReaderContract.IcfactorDataEntry.COLUMN_NAME_FACTOR));
        String qcFactorId = String.valueOf(qcFactorTable.getInt(qcFactorTable.getColumnIndexOrThrow(IcfactorReaderContract.IcfactorEntry.COLUMN_NAME_QCFACTORID)));
        String order = String.valueOf(qcFactorTable.getInt(qcFactorTable.getColumnIndexOrThrow(IcfactorReaderContract.IcfactorEntry.COLUMN_NAME_ORDER)));
        int qCFactorTypeInt = qcFactorDataTable.getInt(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataReaderContract.IcfactorDataEntry.COLUMN_NAME_BUTTON));
        String sl = String.valueOf(qcFactorDataTable.getInt(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataReaderContract.IcfactorDataEntry.COLUMN_NAME_SL)));
        String m = String.valueOf(qcFactorDataTable.getInt(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataReaderContract.IcfactorDataEntry.COLUMN_NAME_M)));
        String s = String.valueOf(qcFactorDataTable.getInt(qcFactorDataTable.getColumnIndexOrThrow(IcfactorDataReaderContract.IcfactorDataEntry.COLUMN_NAME_S)));

        return "<QcontrolDetailFactor>" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                "<Id>0</Id>" +
                "<QcontrolDetailId>" + qControlDetailId + "</QcontrolDetailId>" +
                "<QcFactorId>" + qcFactorId + "</QcFactorId>" +
                "<QcFactorDescription>" + qCFactorDescriptionString + "</QcFactorDescription>" +
                "<QcFactorValue1>" + sl + "</QcFactorValue1>" +
                "<QcFactorValue2>" + (qCFactorTypeInt == 1 ? "" : m) + "</QcFactorValue2>" +
                "<QcFactorValue3>" + (qCFactorTypeInt == 1 ? "" : s) + "</QcFactorValue3>" +
                "<QcFactorType>" + (qCFactorValueString.equals("Quality") ? "Q" : (qCFactorValueString.equals("Condition") ? "C" : "Q&amp;C")) + "</QcFactorType>" +
                "<Order>" + order + "</Order>" +
                "</QcontrolDetailFactor>";
    }

    public String CreateQControlDetailPicture(String qControlDetailId, Cursor pictures) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String fileNameString = pictures.getString(pictures.getColumnIndexOrThrow(PictureReaderContract.PictureEntry.COLUMN_NAME_NAME));
        String directory = activity.getDir("images", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + fileNameString;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(directory, options);
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
        String pictureString = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);

        return "<QcontrolDetailPicture>" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                "<Id>0</Id>" +
                "<FileName>" + fileNameString + "</FileName>" +
                "<Picture> </Picture>" +
                "<QcontrolDetailId>" + qControlDetailId + "</QcontrolDetailId>" +
                "</QcontrolDetailPicture>";
    }

    /*public String CreatePicture(Cursor pictures) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String fileNameString = pictures.getString(pictures.getColumnIndexOrThrow(PictureEntry.COLUMN_NAME_NAME));
        String directory = activity.getDir("images", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + fileNameString;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(directory, options);
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
        String pictureString = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);

        return pictureString;
    }*/
    public String CreatePicture(Cursor pictures , Bitmap bitmap) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String fileNameString = pictures.getString(pictures.getColumnIndexOrThrow(PictureReaderContract.PictureEntry.COLUMN_NAME_NAME));
        String directory = activity.getDir("images", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + fileNameString;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //Bitmap bitmap = BitmapFactory.decodeFile(directory, options);
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
        String pictureString = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);

        return pictureString;
    }

    /*private void scalePicture(String pictureName) throws IOException {
        File f = new File(activity.getDir("images", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + pictureName);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        FileOutputStream fos = new FileOutputStream(f);

        // Scale Picture
        Bitmap scale = null;
        long fileSize = f.length() / 1024;
        while (fileSize > 200) {
            scale = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 3 / 4, bitmap.getHeight() * 3 / 4, true);
            fos = new FileOutputStream(f);
            scale.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            fileSize = f.length() / 1024;
        }
        fos.close();
    }*/
    private Bitmap scalePicture(String pictureName) throws IOException {
        // Rotate Picture
        File f = new File(activity.getDir("images", Context.MODE_PRIVATE) + File.separator + pictureName);
        Bitmap bitmap= decodeFile(f);

/*
	//*long fileSize = f.length() / 1024;
		Boolean existe = f.exists(); //Si es verdadero existe el fichero

        BitmapFactory.Options options = new BitmapFactory.Options();
        //Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
		Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);


        FileOutputStream fos = new FileOutputStream(f);

        existe = f.exists();
		existe = f.isFile();
		f.setReadable(true);
		f.setWritable(true);

        // Scale Picture
        Bitmap scale = null;
		long fileSize= f.length()/1024;
        while (fileSize > 200) {
        	scale = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 3 / 4, bitmap.getHeight() * 3 / 4, true);
        	fos = new FileOutputStream(f);
            scale.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            fileSize = f.length() / 1024;
        }
        fos.close();*/
        boolean deleted = f.delete();
        return bitmap;
    }

    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=120;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
}

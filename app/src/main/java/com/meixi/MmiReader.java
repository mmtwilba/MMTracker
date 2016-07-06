package com.meixi;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class MmiReader {
    private RandomAccessFile m_MmiFile;
    long m_StoredFP;
    public boolean m_bIsOpen;
    int m_iCategoriesCount;
    int m_iDataBlocksCount;
    int m_iFileFormatVersion;
    int m_iMagicNumber;
    long m_lPointerDataBlocks;
    public String m_sFileName;
    boolean runs;

    private static native void CloseMmi();

    private static native boolean OpenMmi(String str);

    private static native int[] ReadBlocks(int i, long j, int i2, String str);

    static {
        System.loadLibrary("mmiread");
    }

    boolean JniOpenMmi(String sFilename) {
        return OpenMmi(sFilename);
    }

    void JniCloseMmi() {
        CloseMmi();
    }

    int[] JniReadBlocks(int iReadCount, long lStartPointer, int iCategory, String sSearchString) {
        return ReadBlocks(iReadCount, lStartPointer, iCategory, sSearchString);
    }

    MmiReader() {
        this.runs = false;
        this.m_bIsOpen = false;
    }

    protected void finalize() throws Throwable {
        Close();
        super.finalize();
    }

    void Pause() {
        if (this.m_bIsOpen) {
            try {
                this.m_StoredFP = this.m_MmiFile.getFilePointer();
                this.m_MmiFile.close();
            } catch (Exception e) {
            }
        }
    }

    void UnPause() {
        if (this.m_bIsOpen) {
            try {
                this.m_MmiFile = new RandomAccessFile(this.m_sFileName, "r");
                this.m_MmiFile.seek(this.m_StoredFP);
            } catch (Exception e) {
            }
        }
    }

    void Close() {
        if (this.m_bIsOpen) {
            try {
                this.m_MmiFile.close();
                this.m_sFileName = "";
            } catch (Exception e) {
            }
        }
        this.m_bIsOpen = false;
    }

    boolean Open(String sFileName) {
        if (this.m_bIsOpen) {
            try {
                this.m_MmiFile.close();
                this.m_sFileName = "";
            } catch (Exception e) {
            }
        }
        this.m_bIsOpen = false;
        try {
            this.m_MmiFile = new RandomAccessFile(sFileName, "r");
            this.m_sFileName = sFileName;
            ReadMetaData(this.m_MmiFile);
            this.m_lPointerDataBlocks = -1;
            this.m_bIsOpen = true;
            return true;
        } catch (Exception e2) {
            return false;
        }
    }

    private boolean ReadMetaData(RandomAccessFile file) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        try {
            file.seek(0);
            try {
                file.read(buf.array());
                this.m_iMagicNumber = buf.getInt(0);
                file.read(buf.array());
                this.m_iFileFormatVersion = buf.getInt(0);
                file.read(buf.array());
                this.m_iCategoriesCount = buf.getInt(0);
                file.read(buf.array());
                this.m_iDataBlocksCount = buf.getInt(0);
                return true;
            } catch (Exception e) {
                return false;
            }
        } catch (IOException e2) {
            return false;
        }
    }

    public boolean ReadCategories(ArrayList<MmiCategory> categories) {
        if (!this.m_bIsOpen) {
            return false;
        }
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        try {
            MmiCategory cat;
            this.m_MmiFile.seek(16);
            if (categories != null) {
                try {
                    cat = new MmiCategory();
                    cat.m_iNumber = -1;
                    cat.m_sName = "<All>";
                    categories.add(cat);
                } catch (Exception e) {
                    return false;
                }
            }
            for (int iNum = 0; iNum < this.m_iCategoriesCount; iNum++) {
                this.m_MmiFile.read(buf.array());
                int iBlockLen = buf.getInt(0);
                this.m_MmiFile.skipBytes(20);
                int iStrLen = iBlockLen - 24;
                byte[] text_buf = new byte[iStrLen];
                this.m_MmiFile.read(text_buf);
                iStrLen -= 8;
                while (text_buf[iStrLen] != null) {
                    iStrLen++;
                }
                if (categories != null) {
                    cat = new MmiCategory();
                    cat.m_iNumber = iNum;
                    cat.m_sName = new String(text_buf, 0, iStrLen, "8859-1");
                    categories.add(cat);
                }
            }
            this.m_lPointerDataBlocks = this.m_MmiFile.getFilePointer();
            return true;
        } catch (IOException e2) {
            return false;
        }
    }

    boolean ResetDataPointer() {
        if (this.m_lPointerDataBlocks > -1) {
            try {
                this.m_MmiFile.seek(this.m_lPointerDataBlocks);
            } catch (Exception e) {
                return false;
            }
        }
        try {
            this.m_MmiFile.seek(0);
            ReadMetaData(this.m_MmiFile);
            ReadCategories(null);
        } catch (Exception e2) {
            return false;
        }
        return true;
    }

    public int ReadDataIndexed(ArrayList<MmiDataBlock> mmi_data, int[] IndexArray) {
        int iLoadedTotal = 0;
        int i = 0;
        ByteBuffer buf4 = ByteBuffer.allocate(4);
        ByteBuffer buf8 = ByteBuffer.allocate(8);
        buf4.order(ByteOrder.LITTLE_ENDIAN);
        buf8.order(ByteOrder.LITTLE_ENDIAN);
        while (IndexArray[i] != -1) {
            try {
                this.m_MmiFile.seek((long) IndexArray[i]);
                this.m_MmiFile.read(buf4.array());
                int iBlockLen = buf4.getInt(0);
                this.m_MmiFile.read(buf4.array());
                int iCatNum = buf4.getInt(0);
                this.m_MmiFile.read(buf8.array());
                double dLat = buf8.getDouble(0);
                this.m_MmiFile.read(buf8.array());
                double dLon = buf8.getDouble(0);
                int iStrLen = iBlockLen - 24;
                byte[] text_buf = new byte[iStrLen];
                this.m_MmiFile.read(text_buf);
                iStrLen -= 8;
                while (text_buf[iStrLen] != null) {
                    iStrLen++;
                }
                String str = new String(text_buf, 0, iStrLen, "8859-1");
                MmiDataBlock data = new MmiDataBlock();
                data.m_iCategory = iCatNum;
                data.m_dLatitude = dLat;
                data.m_dLongitude = dLon;
                data.m_sName = str;
                mmi_data.add(data);
                iLoadedTotal++;
                i++;
            } catch (Exception e) {
                return iLoadedTotal;
            }
        }
        try {
            this.m_MmiFile.seek((long) IndexArray[i + 1]);
            return IndexArray[i + 2];
        } catch (Exception e2) {
            return iLoadedTotal;
        }
    }

    public int ReadData(ArrayList<MmiDataBlock> mmi_data, int iCount, int iReadCategory, String sCurrentSearchText) {
        int iLoadedTotal = 0;
        if (!this.m_bIsOpen) {
            return 0;
        }
        ByteBuffer buf4 = ByteBuffer.allocate(4);
        ByteBuffer buf8 = ByteBuffer.allocate(8);
        buf4.order(ByteOrder.LITTLE_ENDIAN);
        buf8.order(ByteOrder.LITTLE_ENDIAN);
        int iNum = 0;
        while (iNum < iCount) {
            try {
                this.m_MmiFile.read(buf4.array());
                int iBlockLen = buf4.getInt(0);
                this.m_MmiFile.read(buf4.array());
                int iCatNum = buf4.getInt(0);
                if (iReadCategory <= -1 || iReadCategory == iCatNum) {
                    this.m_MmiFile.read(buf8.array());
                    double dLat = buf8.getDouble(0);
                    this.m_MmiFile.read(buf8.array());
                    double dLon = buf8.getDouble(0);
                    int iStrLen = iBlockLen - 24;
                    byte[] text_buf = new byte[iStrLen];
                    this.m_MmiFile.read(text_buf);
                    iStrLen -= 8;
                    while (text_buf[iStrLen] != null) {
                        iStrLen++;
                    }
                    String str = new String(text_buf, 0, iStrLen, "8859-1");
                    if (sCurrentSearchText == "" || str.toUpperCase().contains(sCurrentSearchText.toUpperCase())) {
                        MmiDataBlock data = new MmiDataBlock();
                        data.m_iCategory = iCatNum;
                        data.m_dLatitude = dLat;
                        data.m_dLongitude = dLon;
                        data.m_sName = str;
                        mmi_data.add(data);
                        iLoadedTotal++;
                    }
                } else {
                    this.m_MmiFile.skipBytes(iBlockLen - 8);
                }
                iNum++;
            } catch (Exception e) {
                return 0;
            }
        }
        return iLoadedTotal;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.file;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;

/**
 *
 * @author KH
 */
@Named("fileList")
public class FileListBean {

    private List<SecaFileEntity> files;
    private List<SecaFileEntity> largeFiles;
    private List<SecaFileSequence> sequences;

    public FileListBean() {

        sequences = new ArrayList<SecaFileSequence>();
        //Players  
        files = new ArrayList<SecaFileEntity>();
        SecaFileEntity file1 = new SecaFileEntity();
        SecaFileEntity file2 = new SecaFileEntity();
        SecaFileEntity file3 = new SecaFileEntity();
        SecaFileEntity file4 = new SecaFileEntity();
        SecaFileEntity file5 = new SecaFileEntity();
        SecaFileEntity file6 = new SecaFileEntity();
        SecaFileEntity file7 = new SecaFileEntity();
        /*file1.randInit();file1.setFILENAME("7 liner 1");file1.setCREATED_BY("Alex");
        file2.randInit();file2.setFILENAME("6 liner 1");file2.setCREATED_BY("Alex");
        file3.randInit();file3.setFILENAME("6 liner 2");file3.setCREATED_BY("Alex");
        file4.randInit();file4.setFILENAME("6 liner 3");file4.setCREATED_BY("Alex");
        file5.randInit();file5.setFILENAME("6 liner 4");file5.setCREATED_BY("Alex");
        file6.randInit();file6.setFILENAME("8 liner 1");file6.setCREATED_BY("Alex");
        file7.randInit();file7.setFILENAME("Special Characters");file7.setCREATED_BY("Alex");*/
        
        files.add(file1);
        files.add(file2);
        files.add(file3);
        files.add(file4);
        files.add(file5);
        files.add(file6);
        files.add(file7);
        
        largeFiles = new ArrayList<SecaFileEntity>();
        for(int i=0;i<100;i++){
            SecaFileEntity fileTemp = new SecaFileEntity();
            //fileTemp.randInit();fileTemp.setFILENAME("X liner "+i);//fileTemp.setCREATED_BY("Alex");
            largeFiles.add(fileTemp);
        }
        String initialSequence = "\" 1\" \" 2\" \" 3\" \" 4\" \" 5\" \" 6\"";
        int sequence_index = 1;
        for(int i=7; i<45; i++){
            String num = i+1+"";
            if(i<10)
                num = " ".concat(num);
            String addSequence = initialSequence.concat(" \"").concat(num).concat("\"");
            SecaFileSequence fs = new SecaFileSequence();
            //fs.randInit();
            //fs.setSEQUENCE_LINE(addSequence);
            //fs.setSEQNUM(sequence_index++);
            sequences.add(fs);
        }
    }

    public List<SecaFileEntity> getFiles() {
        return files;
    }

    public void setPlayers(List<SecaFileEntity> files) {
        this.files = files;
    }

    public List<SecaFileEntity> getLargeFiles() {
        return largeFiles;
    }

    public void setLargeFiles(List<SecaFileEntity> largeFiles) {
        this.largeFiles = largeFiles;
    }

    public List<SecaFileSequence> getSequences() {
        return sequences;
    }

    public void setSequences(List<SecaFileSequence> sequences) {
        this.sequences = sequences;
    }
    
    
}

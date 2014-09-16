/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.data;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import javax.persistence.Entity;

/**
 *
 * @author vincent.a.lee
 */
public class EntityExplorer {
    
    public List<Class> getClasses(String packageName) throws Exception {
        File directory = null;
        try {
            ClassLoader cld = getClassLoader();
            URL resource = getResource(packageName, cld);
            directory = new File(resource.getFile());
        } catch (NullPointerException ex) {
            throw new ClassNotFoundException(packageName + " (" + directory
                    + ") does not appear to be a valid package");
        }
        return collectClasses(packageName, directory);
    }

    public ClassLoader getClassLoader() throws ClassNotFoundException {
        ClassLoader cld = Thread.currentThread().getContextClassLoader();
        if (cld == null) {
            throw new ClassNotFoundException("Can't get class loader.");
        }
        return cld;
    }

    public URL getResource(String packageName, ClassLoader cld) throws ClassNotFoundException {
        String path = packageName.replace('.', '/');
        URL resource = cld.getResource(path);
        if (resource == null) {
            throw new ClassNotFoundException("No resource for " + path);
        }
        return resource;
    }

    public List<Class> collectClasses(String packageName, File directory) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (directory.exists()) {
            String[] files = directory.list(); // returns a list of files and directories.
            for (String file : files) {
                
                if (file.endsWith(".class")) {
                    // removes the .class extension
                    Class c = Class.forName(packageName + '.'+ file.substring(0, file.length() - 6));
                    //only add annotated classes
                    if(c.isAnnotationPresent(Entity.class))
                        classes.add(c);
                }
            }
        } else {
            throw new ClassNotFoundException(packageName
                    + " is not a valid package");
        }
        return classes;
    }
    
    public List<File> collectFiles(File directory, String extension){
        List<File> found = new ArrayList<File>();
        List<File> childrenFiles = Arrays.asList(directory.listFiles());
        
        for(File childFile : childrenFiles){
            if(childFile.isDirectory()){
                found.addAll(collectFiles(childFile,extension));
            }
            else{
                if(childFile.getName().endsWith(extension)){
                    found.add(childFile);
                }
            }
        }
        return found;
    }
    
    public List<Class> collectClasses(File directory, ClassLoader loader){
        List<Class> found = new ArrayList<Class>();
        List<File> childrenFiles = Arrays.asList(directory.listFiles());
        
        for(File childFile : childrenFiles){
            if(childFile.isDirectory()){
                found.addAll(collectClasses(childFile,loader));
            }
            else{
                if(childFile.getName().endsWith(".class")){
                    URL url = loader.getResource(childFile.getName().substring(0, (int) (childFile.getName().length() - 6)));
                }
                
            }
        }
        return found;
    }
    
    public void collectPackages(File directory, Package currentTree, List<Package> found) throws CloneNotSupportedException{
        List<File> childrenFiles = Arrays.asList(directory.listFiles());
        boolean leaf = true;
        
         for(File childFile : childrenFiles){
            if(childFile.isDirectory()){
                //add the package as a parent first, then go into the next level
                leaf = false;
                currentTree.push(childFile.getName());
                collectPackages(childFile,currentTree,found);
                currentTree.pop();
            }
        }
        if(leaf){
            found.add(currentTree.clone());
        }
            
    }
    
    public static void main(String[] args) throws ClassNotFoundException, IOException, Exception{
        /*EntityExplorer explorer = new EntityExplorer();
        System.out.println("Run test");
        ClassLoader loader = explorer.getClassLoader();
        URL entityDirectory = explorer.getResource("seca2.entity", loader);
        System.out.println(entityDirectory);
        
        List<File> found = explorer.collectFiles(new File(entityDirectory.getFile()), ".class");
        
        for(File f:found){
            String filename = f.getName();
            System.out.println(filename);
            Class T1 = Class.forName("seca2.entity.file."+filename.substring(0, (int) (filename.length() - 6)));
            Class T = loader.loadClass("seca2.entity.file."+filename.substring(0, (int) (filename.length() - 6)));
            System.out.println(T.getName());
        }*/
        
        /*
        EntityExplorer explorer = new EntityExplorer();
        
        List<Class> fileClasses = explorer.getClasses("seca2.entity.file");
        
        for(Class c:fileClasses){
            if(c.isAnnotationPresent(Entity.class))
                System.out.println(c.getName());
        }*/
        
        EntityExplorer explorer = new EntityExplorer();
        ClassLoader loader = explorer.getClassLoader();
        URL entityDirectory = explorer.getResource("seca2.entity", loader);
        System.out.println(entityDirectory);
        
        List<Package> found = new ArrayList<Package>();
        Package currentTree = new Package();
        
        currentTree.push("seca2");
        currentTree.push("entity");
        explorer.collectPackages(new File(entityDirectory.getFile()), currentTree, found);
        
        List<Class> foundClass = new ArrayList<Class>();
        System.out.println("All packages found:");
        for(Package f:found){
            System.out.println(f);
            foundClass.addAll(explorer.getClasses(f.toString()));
        }
        System.out.println("All classes found:");
        for(Class c:foundClass){
            System.out.println(c.getName());
        }
        
    }
}
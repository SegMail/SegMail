/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.utilities;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import javax.persistence.Entity;
import seca2.bootstrap.BootstrapModule;

/**
 *
 * @author vincent.a.lee
 */
public class EntityExplorer {
    
    public static List<Class> getClasses(String packageName) throws Exception {
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

    public static ClassLoader getClassLoader() throws ClassNotFoundException {
        ClassLoader cld = Thread.currentThread().getContextClassLoader();
        if (cld == null) {
            throw new ClassNotFoundException("Can't get class loader.");
        }
        return cld;
    }

    public static URL getResource(String packageName, ClassLoader cld) throws ClassNotFoundException {
        String path = packageName.replace('.', '/');
        URL resource = cld.getResource(path);
        if (resource == null) {
            throw new ClassNotFoundException("No resource for " + path);
        }
        return resource;
    }

    public static List<Class> collectClasses(String packageName, File directory) throws ClassNotFoundException {
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
    
    public static List<File> collectFiles(File directory, String extension){
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
    
    public static List<Class> collectClasses(File directory, ClassLoader loader){
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
    
    public static void collectPackages(File directory, Package currentTree, List<Package> found) throws CloneNotSupportedException{
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
    
    public static List<Class> collectClasses(Package root, ClassLoader loader) throws ClassNotFoundException, CloneNotSupportedException, Exception{
        URL entityDirectory = getResource(root.toString(), loader);
        List<Package> found = new ArrayList<Package>();
        Package currentTree = new Package();
        
        collectPackages(new File(entityDirectory.getFile()), root, found);
        List<Class> foundClasses = new ArrayList<Class>();
        for(Package f:found){
            foundClasses.addAll(getClasses(f.toString()));
        }
        
        return foundClasses;
    }
    
    public static List<Class> collectEntities(Package root, ClassLoader loader) throws CloneNotSupportedException, Exception{
        List<Class> found = collectClasses(root, loader);
        List<Class> entities = new ArrayList<Class>();
        for(Class f:found){
            if(f.isAnnotationPresent(Entity.class))
                entities.add(f);
        }
        
        return entities;
    }
    
    public static List<Class> collectBootstrapModules(Package root, ClassLoader loader) throws CloneNotSupportedException, Exception{
        List<Class> found = collectClasses(root, loader);
        List<Class> entities = new ArrayList<Class>();
        for(Class f:found){
            if(f.isAssignableFrom(BootstrapModule.class))
                entities.add(f);
        }
        
        return entities;
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
        /*ClassLoader loader = explorer.getClassLoader();
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
        }*/
        Package root = new Package();
        root.push("seca2");
        root.push("entity");
        
        ClassLoader loader = explorer.getClassLoader();
        
        System.out.println("All entities found:");
        List<Class> foundEntities = explorer.collectEntities(root, loader);
        for(Class c:foundEntities){
            System.out.println(c.getName());
        }
        
    }
}
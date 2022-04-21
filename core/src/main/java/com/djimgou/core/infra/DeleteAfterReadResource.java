package com.djimgou.core.infra;

import org.springframework.core.io.Resource;

import java.io.*;
import java.net.URI;
import java.net.URL;

public class DeleteAfterReadResource implements Resource {
    private Resource resource;
    public DeleteAfterReadResource(Resource resource){
        this.resource = resource;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new DeleteOnCloseFileInputStream(resource.getFile());
    }

    @Override
    public boolean exists() {
        return resource.exists();
    }

    @Override
    public URL getURL() throws IOException {
        return resource.getURL();
    }

    @Override
    public URI getURI() throws IOException {
        return resource.getURI();
    }

    @Override
    public File getFile() throws IOException {
        return resource.getFile();
    }

    @Override
    public long contentLength() throws IOException {
        return resource.contentLength();
    }

    @Override
    public long lastModified() throws IOException {
        return resource.lastModified();
    }

    @Override
    public Resource createRelative(String s) throws IOException {
        return resource.createRelative(s);
    }

    @Override
    public String getFilename() {
        return resource.getFilename();
    }

    @Override
    public String getDescription() {
        return resource.getDescription();
    }

    private static final class DeleteOnCloseFileInputStream extends FileInputStream {

        private File file;
        DeleteOnCloseFileInputStream(File file) throws FileNotFoundException {
            super(file);
            this.file = file;
        }

        @Override
        public void close() throws IOException {
            super.close();
            file.delete();
        }
    }


}

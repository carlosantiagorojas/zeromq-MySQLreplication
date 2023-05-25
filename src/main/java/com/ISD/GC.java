package com.ISD;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

public class GC{

    private ZContext context;
    private ZMQ.Socket socket;
    private ZMQ.Socket publisher;
    private ZMQ.Socket socketSolicitar;
    private ZMQ.Socket socketSolicitarPH;

    public GC()
    {

    }

    public GC(ZContext context, Socket socket, Socket publisher, Socket socketSolicitar) {
        this.context = context;
        this.socket = socket;
        this.publisher = publisher;
        this.socketSolicitar = socketSolicitar;
    }

    public ZContext getContext() {
        return context;
    }

    public void setContext(ZContext context) {
        this.context = context;
    }

    public ZMQ.Socket getSocket() {
        return socket;
    }

    public void setSocket(ZMQ.Socket socket) {
        this.socket = socket;
    }

    public ZMQ.Socket getPublisher() {
        return publisher;
    }

    public void setPublisher(ZMQ.Socket publisher) {
        this.publisher = publisher;
    }

    public ZMQ.Socket getSocketSolicitar() {
        return socketSolicitar;
    }

    public void setSocketSolicitar(ZMQ.Socket socketSolicitar) {
        this.socketSolicitar = socketSolicitar;
    }

    public ZMQ.Socket getSocketSolicitarPH() {
        return socketSolicitarPH;
    }

    public void setSocketSolicitarPH(ZMQ.Socket socketSolicitarPH) {
        this.socketSolicitarPH = socketSolicitarPH;
    }

   
}


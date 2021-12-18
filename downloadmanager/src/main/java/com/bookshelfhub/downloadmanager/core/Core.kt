package com.bookshelfhub.downloadmanager.core

class Core() {

    private var executorSupplier: ExecutorSupplier = DefaultExecutorSupplier()

    companion object{
        private var instance: Core? = null

        fun getInstance(): Core {
            if (instance == null) {
                synchronized(Core::class.java) {
                    if (instance == null) {
                        instance = Core()
                    }
                }
            }
            return instance!!
        }

        fun shutDown() {
            if (instance != null) {
                instance = null
            }
        }
    }


    fun getExecutorSupplier(): ExecutorSupplier {
        return executorSupplier
    }



}
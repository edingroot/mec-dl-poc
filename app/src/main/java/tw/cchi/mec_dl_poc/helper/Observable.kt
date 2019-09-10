package tw.cchi.mec_dl_poc.helper

import java.util.Observable

class Observable : Observable() {

    override fun notifyObservers() {
        setChanged()
        super.notifyObservers()
    }

    override fun notifyObservers(arg: Any?) {
        setChanged()
        super.notifyObservers(arg)
    }
}

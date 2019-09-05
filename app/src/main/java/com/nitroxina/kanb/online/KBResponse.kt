package com.nitroxina.kanb.online

data class KBResponse(var id: Int, val jsonrpc: String, val result: String?, val error: KBError?)

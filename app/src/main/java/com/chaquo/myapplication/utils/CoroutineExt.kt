package com.chaquo.myapplication.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by Hamza Khalid
 * Principal Software Engineer
 * Created on 16 Feb,2023 10:30
 * Copyright (c) All rights reserved.
 * @see "<a href="https://www.linkedin.com/in/the-hamzakhalid/">Linkedin Profile</a>"
 */


fun mainCoroutine(work: suspend (() -> Unit)): Job {
    return CoroutineScope(Dispatchers.Main).launch {
        work()
    }
}


fun ioCoroutine(work: suspend (() -> Unit)): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        work()
    }
}
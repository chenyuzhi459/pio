package io.sugo.pio.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.sugo.pio.client.*;

/**
 */
public class ServerViewModule implements Module
{
    @Override
    public void configure(Binder binder)
    {
        binder.bind(FilteredServerInventoryView.class)
                .to(BatchServerInventoryView.class);
        binder.bind(BatchServerInventoryView.class).in(ManageLifecycle.class);
    }
}

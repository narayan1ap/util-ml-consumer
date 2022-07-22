import { Component, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AssetProviderLookupComponent } from './components/asset-provider-lookup/asset-provider-lookup.component';

const routes: Routes = [
  {
  path:'asset-provider-lookup',
  component:AssetProviderLookupComponent
}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssetProviderLookupComponent } from './asset-provider-lookup.component';

describe('AssetProviderLookupComponent', () => {
  let component: AssetProviderLookupComponent;
  let fixture: ComponentFixture<AssetProviderLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssetProviderLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssetProviderLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

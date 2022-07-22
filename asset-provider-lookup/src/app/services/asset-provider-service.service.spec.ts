import { TestBed } from '@angular/core/testing';

import { AssetProviderServiceService } from './asset-provider-service.service';

describe('AssetProviderServiceService', () => {
  let service: AssetProviderServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AssetProviderServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

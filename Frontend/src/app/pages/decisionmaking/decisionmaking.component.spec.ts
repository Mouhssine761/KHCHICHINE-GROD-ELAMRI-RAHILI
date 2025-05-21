import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecisionmakingComponent } from './decisionmaking.component';

describe('DecisionmakingComponent', () => {
  let component: DecisionmakingComponent;
  let fixture: ComponentFixture<DecisionmakingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionmakingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DecisionmakingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
